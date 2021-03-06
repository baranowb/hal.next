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
package org.jboss.hal.client.configuration.subsystem;

import javax.inject.Inject;

import org.jboss.hal.core.modelbrowser.ModelBrowser;
import org.jboss.hal.core.mvp.PatternFlyViewImpl;
import org.jboss.hal.dmr.model.ResourceAddress;

/**
 * @author Harald Pehl
 */
public class SubsystemView extends PatternFlyViewImpl implements SubsystemPresenter.MyView {

    private final ModelBrowser modelBrowser;

    @Inject
    public SubsystemView(ModelBrowser modelBrowser) {
        this.modelBrowser = modelBrowser;
        initElements(modelBrowser);
    }

    @Override
    public void setRoot(final ResourceAddress root) {
        modelBrowser.setRoot(root, false);
    }
}
