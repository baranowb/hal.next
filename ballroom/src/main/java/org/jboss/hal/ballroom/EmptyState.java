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
package org.jboss.hal.ballroom;

import com.google.gwt.safehtml.shared.SafeHtml;
import elemental.client.Browser;
import elemental.dom.Element;
import elemental.events.EventListener;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.gwt.elemento.core.IsElement;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.gwt.elemento.core.EventType.click;
import static org.jboss.hal.resources.CSS.*;

/**
 * @author Harald Pehl
 */
public class EmptyState implements IsElement {

    /**
     * Simple data struct for a title which is bound to an event listener (typically onclick). Used in various builders.
     *
     * @author Harald Pehl
     */
    private static class Action {

        public final String title;
        public final EventListener listener;

        public Action(final String title, final EventListener listener) {
            this.title = title;
            this.listener = listener;
        }
    }

    public static class Builder {

        private final String title;
        private final List<Element> paragraphs;
        private final List<Action> secondaryActions;
        private String icon;
        private Action primaryAction;

        public Builder(final String title) {
            this.title = title;
            this.paragraphs = new ArrayList<>();
            this.secondaryActions = new ArrayList<>();
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder description(String description) {
            Element p = Browser.getDocument().createElement("p"); //NON-NLS
            p.setTextContent(description);
            paragraphs.add(p);
            return this;
        }

        public Builder description(SafeHtml description) {
            Element p = Browser.getDocument().createElement("p"); //NON-NLS
            p.setInnerHTML(description.asString());
            paragraphs.add(p);
            return this;
        }

        public Builder primaryAction(String title, EventListener listener) {
            this.primaryAction = new Action(title, listener);
            return this;
        }

        public Builder secondaryAction(String title, EventListener listener) {
            this.secondaryActions.add(new Action(title, listener));
            return this;
        }

        public EmptyState build() {
            return new EmptyState(this);
        }
    }


    private final Element root;

    private EmptyState(Builder builder) {
        Elements.Builder eb = new Elements.Builder().div().css(blankSlatePf);
        if (builder.icon != null) {
            eb.div().css(blankSlatePfIcon).start("i").css(builder.icon).end().end();
        }
        eb.h(1).textContent(builder.title).end();
        for (Element paragraph : builder.paragraphs) {
            eb.add(paragraph);
        }
        if (builder.primaryAction != null) {
            eb.div().css(blankSlatePfMainAction)
                    .button().css(btn, btnPrimary, btnLg).on(click, builder.primaryAction.listener)
                    .textContent(builder.primaryAction.title)
                    .end()
                    .end();
        }
        if (!builder.secondaryActions.isEmpty()) {
            eb.div().css(blankSlatePfSecondaryAction);
            for (Action secondaryAction : builder.secondaryActions) {
                eb.button().css(btn, btnDefault).on(click, secondaryAction.listener)
                        .textContent(secondaryAction.title)
                        .end();
            }
            eb.end();
        }
        eb.end();
        root = eb.build();
    }

    @Override
    public Element asElement() {
        return root;
    }
}
