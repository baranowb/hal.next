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
package org.jboss.hal.core.mvp;

import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Interface for views which use JavaScript based PatternFly components like select picker, tooltips or data tables.
 *
 * @author Harald Pehl
 */
public interface PatternFlyView extends HalView {

    /**
     * This method should be called <em>after</em> the view's elements are attached to the DOM. Typically this method
     * is called from {@link PatternFlyPresenter#onReveal()}.
     * <p>
     * Do <em>not</em> use {@link ViewImpl#onAttach()} to initialize PatternFly components. This works for widgets
     * only, but not for elements!
     */
    void attach();
}
