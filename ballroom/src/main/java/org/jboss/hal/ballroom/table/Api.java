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
package org.jboss.hal.ballroom.table;

import java.util.Collections;
import java.util.List;

import elemental.js.util.JsArrayOf;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jboss.hal.ballroom.form.Form;

import static org.jboss.hal.ballroom.js.JsHelper.asList;
import static org.jboss.hal.ballroom.table.DataTable.DESELECT;
import static org.jboss.hal.ballroom.table.DataTable.ROW;
import static org.jboss.hal.ballroom.table.DataTable.SELECT;

/**
 * Subset of the DataTables API.
 *
 * @param <T> the row type
 *
 * @author Harald Pehl
 * @see <a href="https://datatables.net/reference/api/">https://datatables.net/reference/api/</a>
 */
@JsType(isNative = true)
public class Api<T> {

    /**
     * Typesafe enum for the paging parameter of {@link #draw(String)}.
     *
     * @see <a href="https://datatables.net/reference/api/draw()">https://datatables.net/reference/api/draw()</a>
     */
    @SuppressWarnings("HardCodedStringLiteral")
    public enum RefreshMode {

        /**
         * The ordering and search will be recalculated and the rows redrawn in their new positions. The paging will be
         * reset back to the first page.
         */
        RESET("full-reset"),

        /**
         * The ordering and search will be recalculated and the rows redrawn in their new positions. The paging will
         * not be reset - i.e. the current page will still be shown.
         */
        HOLD("full-hold"),

        /**
         * Ordering and search will not be updated and the paging position held where is was. This is useful for paging
         * when data has not been changed between draws.
         */
        PAGE("page");

        private final String mode;

        RefreshMode(final String mode) {
            this.mode = mode;
        }

        public String mode() {
            return mode;
        }
    }


    /**
     * Callback used for all kind of "select" and "deselect" events.
     *
     * @param <T> the row type
     *
     * @see <a href="https://datatables.net/reference/event/select">https://datatables.net/reference/event/select</a>
     * @see <a href="https://datatables.net/reference/event/deselect">https://datatables.net/reference/event/deselect</a>
     */
    @JsFunction
    @FunctionalInterface
    public interface SelectCallback<T> {

        void onSelect(Object event, Api<T> api, String type);
    }


    /**
     * Convenience handler when a <em>row</em> is selected.
     *
     * @param <T> the row type
     */
    @FunctionalInterface
    public interface SelectionHandler<T> {

        /**
         * Called when a <em>row</em> is selected.
         *
         * @param api the api instance
         * @param row the selected row.
         */
        void onSelect(Api<T> api, T row);
    }


    /**
     * Convenience handler when a <em>row</em> is deselected.
     *
     * @param <T> the row type
     */
    @FunctionalInterface
    public interface DeselectionHandler<T> {

        /**
         * Called when a <em>row</em> is deselected.
         *
         * @param api the api instance
         */
        void onDeselect(Api<T> api);
    }


    /**
     * Convenience handler when a <em>row</em> selection <em>or</em> deselection takes place.
     *
     * @param <T> the row type
     */
    @FunctionalInterface
    public interface SelectionChangeHandler<T> {

        /**
         * Called when a selection changed. That is when a row is selected <em>or</em> deselected.
         *
         * @param api the api instance
         */
        void onSelectionChanged(Api<T> api);
    }


    // We cannot have both a property and a method named equally.
    // That's why the API defines the property "row" and the method "rows"
    @JsProperty Row<T> row;


    // ------------------------------------------------------ API a-z

    public native Api<T> button(int index);

    public native Api<T> clear();

    public native Api<T> data();

    public native Api<T> draw(String paging);

    /**
     * Disables or enables the button selected with {@link #button(int)}
     */
    public native Api<T> enable(boolean enable);

    /**
     * Adds a selection callback. Currently restricted to the "select" and "deselect" event.
     *
     * @param event    must be "select" or "deselect"
     * @param callback the select callback
     */
    public native Api<T> on(String event, SelectCallback callback);

    public native Api<T> off(String event);

    /**
     * Select all rows, but apply the specified modifier (e.g. to return only selected rows). Chain the {@link #data()}
     * to get the actual data.
     */
    public native Api<T> rows(SelectorModifier selectorModifier);

    /**
     * Select rows by index. Chain the {@link #data()} to get the actual data.
     */
    public native Api<T> rows(int index);

    /**
     * Select rows by using a function. Chain the {@link #data()} to get the actual data.
     */
    public native Api<T> rows(RowSelection<T> selection);

    /**
     * Selects the row(s) that have been found by the {@link #rows(RowSelection)}, {@link #rows(int)} or {@link
     * #rows(SelectorModifier)} selector methods.
     */
    public native Api<T> select();

    public native JsArrayOf<T> toArray();


    // ------------------------------------------------------ overlay methods

    @JsOverlay
    public final Api<T> add(T data) {
        return row.add(data);
    }

    @JsOverlay
    public final Api<T> add(Iterable<T> data) {
        if (data != null) {
            for (T d : data) {
                row.add(d);
            }
        }
        return this;
    }

    @JsOverlay
    public final boolean hasSelection() {
        return !selectedRows().isEmpty();
    }

    @JsOverlay
    public final Api<T> onSelect(SelectionHandler<T> handler) {
        on(SELECT, new SelectCallback<T>() {
            @Override
            public void onSelect(final Object event, final Api<T> api, final String type) {
                if (ROW.equals(type)) {
                    handler.onSelect(api, api.selectedRow());
                }
            }
        });
        return this;
    }

    @JsOverlay
    public final Api<T> onDeselect(DeselectionHandler<T> handler) {
        on(DESELECT, new SelectCallback<T>() {
            @Override
            public void onSelect(final Object event, final Api<T> api, final String type) {
                if (ROW.equals(type)) {
                    handler.onDeselect(api);
                }
            }
        });
        return this;
    }

    @JsOverlay
    public final Api<T> onSelectionChange(SelectionChangeHandler<T> handler) {
        on(SELECT, new SelectCallback<T>() {
            @Override
            public void onSelect(final Object event, final Api<T> api, final String type) {
                if (ROW.equals(type)) {
                    handler.onSelectionChanged(api);
                }
            }
        });
        on(DESELECT, new SelectCallback<T>() {
            @Override
            public void onSelect(final Object event, final Api<T> api, final String type) {
                if (ROW.equals(type)) {
                    handler.onSelectionChanged(api);
                }
            }
        });
        return this;
    }

    @JsOverlay
    public final Api<T> refresh(RefreshMode mode) {
        return draw(mode.mode());
    }

    @JsOverlay
    public final T selectedRow() {
        List<T> rows = selectedRows();
        if (rows.isEmpty()) {
            return null;
        }
        return rows.get(0);
    }

    @JsOverlay
    public final List<T> selectedRows() {
        SelectorModifier selectorModifier = new SelectorModifierBuilder().selected().build();
        JsArrayOf<T> selection = rows(selectorModifier).data().toArray();
        if (selection == null || selection.isEmpty()) {
            return Collections.emptyList();
        }
        return asList(selection);
    }

    @JsOverlay
    public final Api<T> bindForm(Form<T> form) {
        // don't replace this with a lambda - it won't run in super dev mode
        //noinspection Convert2Lambda
        return onSelectionChange(new SelectionChangeHandler<T>() {
            @Override
            public void onSelectionChanged(final Api<T> api) {
                if (api.hasSelection()) {
                    form.view(api.selectedRow());
                } else {
                    form.clear();
                }
            }
        });
    }

    @JsOverlay
    public final Api<T> bindForms(final Iterable<Form<T>> forms) {
        // don't replace this with a lambda - it won't run in super dev mode
        //noinspection Convert2Lambda
        return onSelectionChange(new SelectionChangeHandler<T>() {
            @Override
            public void onSelectionChanged(final Api<T> api) {
                if (api.hasSelection()) {
                    T selectedRow = api.selectedRow();
                    for (Form<T> form : forms) {
                        form.view(selectedRow);
                    }
                } else {
                    for (Form<T> form : forms) {
                        form.clear();
                    }
                }
            }
        });
    }
}