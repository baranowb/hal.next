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

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;
import static org.jboss.hal.resources.UIConstants.OBJECT;

/**
 * Select options.
 *
 * @author Harald Pehl
 * @see <a href="https://datatables.net/reference/option/#select">https://datatables.net/reference/option/#select</a>
 */
@JsType(isNative = true, namespace = GLOBAL, name = OBJECT)
class Select {

    @JsOverlay
    @SuppressWarnings("HardCodedStringLiteral")
    static Select build(boolean multiselect) {
        Select select = new Select();
        select.info = false;
        select.items = "row";
        select.style = multiselect ? "multi" : "single";
        return select;
    }

    public boolean info;
    public String items;
    public String style;
}
