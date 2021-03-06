/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.ballroom.form;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import elemental.client.Browser;
import elemental.dom.Element;
import elemental.dom.Node;
import elemental.html.ButtonElement;
import elemental.html.DivElement;
import elemental.html.LabelElement;
import elemental.html.ParagraphElement;
import elemental.html.SpanElement;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.hal.ballroom.Attachable;
import org.jboss.hal.ballroom.form.ResolveExpressionEvent.ResolveExpressionHandler;
import org.jboss.hal.ballroom.typeahead.Typeahead;
import org.jboss.hal.resources.CSS;
import org.jboss.hal.resources.Constants;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Messages;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.singletonList;
import static org.jboss.gwt.elemento.core.EventType.click;
import static org.jboss.gwt.elemento.core.InputType.text;
import static org.jboss.hal.ballroom.form.Form.State.EDITING;
import static org.jboss.hal.ballroom.form.Form.State.READONLY;
import static org.jboss.hal.resources.CSS.*;
import static org.jboss.hal.resources.UIConstants.HIDDEN;
import static org.jboss.hal.resources.UIConstants.TABINDEX;

/**
 * TODO Implement org.jboss.hal.ballroom.form.Form.State#READONLY
 * TODO Show resolved expressions using a dismissable inline notification
 * https://www.patternfly.org/patterns/inline-notifications/
 *
 * @author Harald Pehl
 */
public abstract class AbstractFormItem<T> implements FormItem<T> {

    static final Constants CONSTANTS = GWT.create(Constants.class);
    static final Messages MESSAGES = GWT.create(Messages.class);

    private static final String ARIA_DESCRIBEDBY = "aria-describedby";
    private static final String FORM_ITEM_GROUP = "formItemGroup";
    private static final String RESTRICTED = "restricted";
    private static final String RESTRICTED_ELEMENT = "restrictedElement";

    private final EventBus eventBus;
    private final List<FormItemValidation<T>> validationHandlers;
    private final String label;
    private final String hint;
    private boolean required;
    private boolean modified;
    private boolean undefined;
    private boolean restricted;
    private boolean expressionAllowed;
    private SuggestHandler suggestHandler;
    private T defaultValue;

    // Form.State#EDITING elements
    final DivElement editingRoot;
    final DivElement inputGroupContainer;
    final DivElement inputContainer;
    final SpanElement errorText;
    private final LabelElement inputLabelElement;
    private final SpanElement inputButtonContainer;
    private final ButtonElement expressionButton;
    private final ButtonElement showAllButton;
    private final DivElement editingRestricted;
    private final InputElement<T> inputElement;
    private SpanElement inputAddonContainer;

    // Form.State#READONLY elements
    private final LabelElement readonlyLabelElement;
    private final DivElement readonlyRoot;
    private final SpanElement readonlyRestricted;
    protected final DivElement valueContainer;
    protected final ParagraphElement valueElement;


    // ------------------------------------------------------ initialization

    @SuppressWarnings("unchecked")
    protected AbstractFormItem(String name, String label, String hint, InputElement.Context<?> context) {
        this.inputElement = newInputElement(context);

        this.label = label;
        this.hint = hint;
        this.required = false;
        this.modified = false;
        this.undefined = true;
        this.restricted = false;
        this.expressionAllowed = true;

        this.eventBus = new SimpleEventBus();
        this.validationHandlers = new LinkedList<>();
        resetValidationHandlers();

        // editing elements
        editingRoot = new Elements.Builder().div().css(formGroup).end().build();
        inputLabelElement = new Elements.Builder()
                .label(label)
                .title(label)
                .css(controlLabel, halFormLabel)
                .end()
                .build();
        inputContainer = new Elements.Builder()
                .div()
                .css(halFormInput)
                .end()
                .build();
        errorText = new Elements.Builder().span().css(helpBlock).end().build();
        Elements.setVisible(errorText, false);

        inputGroupContainer = new Elements.Builder().div().css(inputGroup).end().build();
        inputButtonContainer = new Elements.Builder().span().css(inputGroupBtn).end().build();
        if (hint != null) {
            inputAddonContainer = new Elements.Builder()
                    .span()
                    .id(Ids.build(name, "addon", "hint"))
                    .css(inputGroupAddon)
                    .textContent(hint)
                    .end().build();
        }

        // @formatter:off
        expressionButton = new Elements.Builder()
            .button().css(btn, btnDefault)
                .on(click, event -> ResolveExpressionEvent.fire(this, getExpressionValue()))
                .title(CONSTANTS.expressionResolver())
                .start("i").css(fontAwesome("link")).end()
            .end().build();

        showAllButton = new Elements.Builder()
            .button().css(btn, btnDefault)
                .on(click, event -> showAll())
                .title(CONSTANTS.showAll())
                .attr(TABINDEX, String.valueOf(-1))
                .start("i").css(fontAwesome("angle-down")).end()
            .end().build();

        Elements.Builder restrictedBuilder = new Elements.Builder()
            .div().css(inputGroup)
                .input(text).id(Ids.build(name, RESTRICTED))
                    .css(formControl, CSS.restricted)
                    .rememberAs(RESTRICTED_ELEMENT)
                .span().css(inputGroupAddon)
                    .start("i").css(fontAwesome("lock")).end()
                .end()
            .end();
        // @formatter:on

        elemental.html.InputElement restrictedInput = restrictedBuilder.referenceFor(RESTRICTED_ELEMENT);
        restrictedInput.setReadOnly(true);
        restrictedInput.setValue(CONSTANTS.restricted());
        editingRestricted = restrictedBuilder.build();

        // readonly elements
        readonlyRoot = new Elements.Builder().div().css(formGroup).end().build();
        readonlyLabelElement = new Elements.Builder()
                .label(label)
                .title(label)
                .css(controlLabel, halFormLabel)
                .end()
                .build();
        valueContainer = new Elements.Builder()
                .div()
                .css(halFormInput)
                .end()
                .build();
        valueElement = new Elements.Builder().p().css(formControlStatic).end().build();
        readonlyRestricted = new Elements.Builder()
                .span()
                .css(fontAwesome("lock"), CSS.restricted)
                .aria(HIDDEN, String.valueOf(true))
                .end()
                .build();

        assembleUI();
        setId(Ids.build(name));
        setName(name);
    }

    /**
     * Assembles the <strong>initial</strong> widgets / containers at creation time based on the default values of this
     * form item.
     */
    protected void assembleUI() {
        if (hint != null) {
            showInputAddon(hint);
        } else {
            inputContainer.appendChild(inputElement.asElement());
        }
        inputContainer.appendChild(errorText);
        editingRoot.appendChild(inputLabelElement);
        editingRoot.appendChild(inputContainer);

        valueContainer.appendChild(valueElement);
        readonlyRoot.appendChild(readonlyLabelElement);
        readonlyRoot.appendChild(valueContainer);
    }

    private void showInputButton(ButtonElement button) {
        inputElement.asElement().removeAttribute(ARIA_DESCRIBEDBY);

        if (hasInputButton()) {
            Elements.removeChildrenFrom(inputButtonContainer);
            inputButtonContainer.appendChild(button);

        } else if (hasInputAddon()) {
            inputGroupContainer.removeChild(inputAddonContainer);
            inputGroupContainer.appendChild(inputButtonContainer);
            inputButtonContainer.appendChild(button);

        } else {
            inputContainer.removeChild(inputElement.asElement());
            Elements.removeChildrenFrom(inputGroupContainer);
            inputButtonContainer.appendChild(button);
            inputGroupContainer.appendChild(inputElement.asElement());
            inputGroupContainer.appendChild(inputButtonContainer);
            inputContainer.insertBefore(inputGroupContainer, inputContainer.getFirstChild());
        }
    }

    private void showInputAddon(String addon) {
        inputAddonContainer.setTextContent(addon);
        inputElement.asElement().setAttribute(ARIA_DESCRIBEDBY, inputAddonContainer.getId());

        if (hasInputButton()) {
            inputGroupContainer.removeChild(inputButtonContainer);
            inputGroupContainer.appendChild(inputAddonContainer);

        } else //noinspection StatementWithEmptyBody
            if (hasInputAddon()) {
                // nothing to do

            } else {
                if (inputContainer.contains(inputElement().asElement())) {
                    inputContainer.removeChild(inputElement().asElement());
                }
                inputGroupContainer.appendChild(inputElement.asElement());
                inputGroupContainer.appendChild(inputAddonContainer);
                inputContainer.appendChild(inputGroupContainer);
            }
    }

    private void removeInputGroup() {
        Elements.removeChildrenFrom(inputGroupContainer);
        Elements.removeChildrenFrom(inputButtonContainer);
        inputContainer.removeChild(inputGroupContainer);
        inputContainer.insertBefore(inputElement.asElement(), errorText);

    }

    private boolean hasInputButton() {
        return inputContainer.contains(inputGroupContainer) &&
                inputGroupContainer.contains(inputButtonContainer) &&
                inputButtonContainer.getChildren().length() > 0;
    }

    private boolean hasInputButton(ButtonElement button) {
        return inputContainer.contains(inputGroupContainer) &&
                inputGroupContainer.contains(inputButtonContainer) &&
                inputButtonContainer.contains(button);
    }

    private boolean hasInputAddon() {
        return inputContainer.contains(inputGroupContainer) &&
                inputGroupContainer.contains(inputAddonContainer) &&
                !isNullOrEmpty(inputAddonContainer.getTextContent());
    }

    /**
     * Subclasses must create and return an input element with proper styles attached to it.
     * Subclasses should register a value change handler on the input element to update the modified / undefined flags
     * and signal changed values using the {@link #signalChange(Object)} method.
     *
     * @return a new input element for this form item
     */
    protected abstract InputElement<T> newInputElement(InputElement.Context<?> context);

    @Override
    public Element asElement(Form.State state) {
        if (state == EDITING) {
            return editingRoot;
        } else if (state == READONLY) {
            return readonlyRoot;
        } else {
            throw new IllegalStateException("Unknown state in FormItem.asElement(" + state + ")");
        }
    }

    /**
     * Calls {@code SuggestHandler.attach()} in case there was one registered. If you override this method, please
     * call {@code super.attach()} to keep this behaviour.
     */
    @Override
    public void attach() {
        inputElement().attach();
        if (suggestHandler instanceof Attachable) {
            ((Attachable) suggestHandler).attach();
        }
        if (suggestHandler instanceof Typeahead) {
            Typeahead.Bridge.select("#" + getId(EDITING)).onChange(event -> {
                String value = ((elemental.html.InputElement) event.getTarget()).getValue();
                onSuggest(value);
            });
        }
    }


    // ------------------------------------------------------ state, name & text

    @Override
    public T getValue() {
        return inputElement.getValue();
    }

    @Override
    public void setValue(final T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(final T value, final boolean fireEvent) {
        inputElement.setValue(value);
        setReadonlyValue(value);
        markDefaultValue(defaultValue != null && (value == null || isNullOrEmpty(String.valueOf(value))),
                defaultValue);
        if (fireEvent) {
            signalChange(value);
        }
        if (hasExpressionScheme(asString(value))) {
            toggleExpressionSupport(true);
        }
    }

    protected void setReadonlyValue(final T value) {
        String text = value == null ? "" : asString(value);
        valueElement.setTextContent(text);
    }

    @Override
    public void clearValue() {
        inputElement.clearValue();
        setReadonlyValue(null);
        markDefaultValue(defaultValue != null, defaultValue);
    }

    @Override
    public void setDefaultValue(final T defaultValue) {
        this.defaultValue = defaultValue;
        inputElement.setPlaceholder(asString(defaultValue));
    }

    void markDefaultValue(final boolean on, final T defaultValue) {
        if (on) {
            Elements.removeChildrenFrom(valueElement);
            valueElement.setTextContent(asString(defaultValue));
            valueElement.getClassList().add(CSS.defaultValue);
            valueElement.setTitle(CONSTANTS.defaultValue());
        } else {
            valueElement.getClassList().remove(CSS.defaultValue);
            valueElement.setTitle("");
        }
    }

    void signalChange(final T value) {
        ValueChangeEvent.fire(this, value);
    }

    @Override
    public void fireEvent(final GwtEvent<?> gwtEvent) {
        eventBus.fireEvent(gwtEvent);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<T> valueChangeHandler) {
        return eventBus.addHandler(ValueChangeEvent.getType(), valueChangeHandler);
    }

    @Override
    public boolean isEmpty() {
        return supportsExpressions()
                ? getValue() == null || isUndefined() || isNullOrEmpty(getText())
                : getValue() == null || isUndefined();
    }

    @Override
    public String getId(final Form.State state) {
        if (state == EDITING) {
            return inputElement.getId();
        } else if (state == READONLY) {
            return valueElement.getId();
        }
        return null;
    }

    @Override
    public void setId(String id) {
        String editId = Ids.build(id, EDITING.name().toLowerCase());
        String readonlyId = Ids.build(id, READONLY.name().toLowerCase());

        inputElement.asElement().setId(editId);
        inputLabelElement.setHtmlFor(editId);
        valueElement.setId(readonlyId);

        asElement(EDITING).getDataset().setAt(FORM_ITEM_GROUP, editId); //NON-NLS
        asElement(READONLY).getDataset().setAt(FORM_ITEM_GROUP, readonlyId); //NON-NLS
    }

    @Override
    public String getName() {
        return inputElement.getName();
    }

    @Override
    public void setName(final String name) {
        inputElement.setName(name);
    }

    @Override
    public String getText() {
        return inputElement.getText();
    }

    @Override
    public void setText(final String text) {
        inputElement.setText(text);
        valueElement.setTextContent(text);
    }

    String asString(T value) {
        return String.valueOf(value);
    }


    // ------------------------------------------------------ validation

    private void resetValidationHandlers() {
        validationHandlers.clear();
        validationHandlers.addAll(defaultValidationHandlers());
    }

    List<FormItemValidation<T>> defaultValidationHandlers() {
        return singletonList(new RequiredValidation<>(this));
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean requiresValidation() {
        if (isRequired()) {
            return true;
        }
        if (isUndefined()) {
            return false;
        }
        return (isModified() && !validationHandlers.isEmpty());
    }

    @Override
    public void addValidationHandler(FormItemValidation<T> validationHandler) {
        if (validationHandler != null) {
            validationHandlers.add(validationHandler);
        }
    }

    @Override
    public boolean validate() {
        if (requiresValidation()) {
            for (FormItemValidation<T> validationHandler : validationHandlers) {
                ValidationResult result = validationHandler.validate(getValue());
                if (!result.isValid()) {
                    showError(result.getMessage());
                    return false;
                }
            }
        }
        clearError();
        return true;
    }

    @Override
    public void clearError() {
        Elements.setVisible(errorText, false);
        editingRoot.getClassList().remove(hasError);
    }

    @Override
    public void showError(String message) {
        editingRoot.getClassList().add(hasError);
        errorText.setInnerText(message);
        Elements.setVisible(errorText, true);
    }


    // ------------------------------------------------------ expressions

    @Override
    public boolean isExpressionAllowed() {
        return expressionAllowed;
    }

    @Override
    public void setExpressionAllowed(final boolean expressionAllowed) {
        this.expressionAllowed = expressionAllowed;
    }

    @Override
    public boolean isExpressionValue() {
        return supportsExpressions() && hasExpressionScheme(getText());
    }

    @Override
    public String getExpressionValue() {
        if (supportsExpressions()) {
            return getText();
        }
        return null;
    }

    @Override
    public void setExpressionValue(String expressionValue) {
        if (supportsExpressions()) {
            toggleExpressionSupport(true);
            setText(expressionValue);
        }
    }

    @Override
    public void addResolveExpressionHandler(ResolveExpressionHandler handler) {
        eventBus.addHandler(ResolveExpressionEvent.getType(), handler);
    }

    private boolean hasExpressionScheme(String value) {
        return value != null && value.startsWith("${") && value.endsWith("}");
    }

    boolean toggleExpressionSupport(boolean on) {
        // only change the UI if expressions are supported and switch is necessary
        if (supportsExpressions() && !isRestricted() && on != hasInputButton(expressionButton)) {
            if (on) {
                showInputButton(expressionButton);
                if (suggestHandler != null) {
                    suggestHandler.close();
                }
            } else {
                if (suggestHandler != null) {
                    showInputButton(showAllButton);
                } else if (hint != null) {
                    showInputAddon(hint);
                } else {
                    removeInputGroup();
                }
            }
            return true;
        }
        return false;
    }


    // ------------------------------------------------------ suggestion handler

    @Override
    public void registerSuggestHandler(final SuggestHandler suggestHandler) {
        this.suggestHandler = suggestHandler;
        this.suggestHandler.setFormItem(this);
        toggleShowAll(true);
    }

    void onSuggest(final String suggestion) {
        // nop
    }

    private void showAll() {
        if (suggestHandler != null) {
            suggestHandler.showAll();
        }
    }

    private void toggleShowAll(final boolean on) {
        if (suggestHandler != null && !restricted && on != hasInputButton(showAllButton)) {
            if (on) {
                showInputButton(showAllButton);
            } else {
                if (supportsExpressions() && hasExpressionScheme(getText())) {
                    showInputButton(expressionButton);
                } else if (hint != null) {
                    showInputAddon(hint);
                } else {
                    removeInputGroup();
                }
            }
        }
    }


    // ------------------------------------------------------ restricted

    @Override
    public boolean isRestricted() {
        return restricted;
    }

    @Override
    public void setRestricted(final boolean restricted) {
        if (this.restricted != restricted) {
            this.restricted = restricted;
            toggleRestricted(restricted);
        }
    }

    void toggleRestricted(final boolean on) {
        if (on) {
            editingRoot.getClassList().add(hasFeedback);
            readonlyRoot.getClassList().add(hasFeedback);
            Node firstChild = inputContainer.getChildren().item(0);
            inputContainer.removeChild(firstChild);
            inputContainer.appendChild(editingRestricted);

            Elements.removeChildrenFrom(valueElement);
            SpanElement span = Browser.getDocument().createSpanElement();
            span.setInnerText(CONSTANTS.restricted());
            valueElement.appendChild(readonlyRestricted);
            valueElement.appendChild(span);

        } else {
            editingRoot.getClassList().remove(hasFeedback);
            readonlyRoot.getClassList().remove(hasFeedback);
            inputContainer.removeChild(editingRestricted);
            Node firstChild = inputContainer.getChildren().item(0);
            if (isExpressionValue()) {
                inputContainer.insertBefore(firstChild, inputGroupContainer);
            } else {
                inputContainer.insertBefore(firstChild, inputElement.asElement());
            }

            Elements.removeChildrenFrom(valueElement);
            setReadonlyValue(getValue());
        }
    }


    // ------------------------------------------------------ input element delegates

    @Override
    public boolean isEnabled() {
        return inputElement.isEnabled();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (enabled) {
            inputContainer.getClassList().remove(disabled);
        } else {
            inputContainer.getClassList().add(disabled);
        }
        inputElement.setEnabled(enabled);
    }

    @Override
    public int getTabIndex() {
        return inputElement.getTabIndex();
    }

    @Override
    public void setTabIndex(final int index) {
        inputElement.setTabIndex(index);
    }

    @Override
    public void setAccessKey(final char accessKey) {
        inputElement.setAccessKey(accessKey);
    }

    @Override
    public void setFocus(final boolean focus) {
        inputElement.setFocus(focus);
    }


    // ------------------------------------------------------ properties

    @Override
    public String getLabel() {
        return inputLabelElement.getInnerText();
    }

    @Override
    public void setLabel(final String label) {
        inputLabelElement.setInnerText(label);
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean required) {
        if (required != this.required) {
            if (required) {
                inputLabelElement.setInnerHTML(label + " " + MESSAGES.requiredMarker().asString());
            } else {
                inputLabelElement.setInnerHTML(label);
            }
        }
        this.required = required;
    }

    @Override
    public final boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public final boolean isUndefined() {
        return undefined;
    }

    @Override
    public void setUndefined(boolean undefined) {
        this.undefined = undefined;
    }

    InputElement<T> inputElement() {
        return inputElement;
    }
}
