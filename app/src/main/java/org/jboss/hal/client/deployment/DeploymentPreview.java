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
package org.jboss.hal.client.deployment;

import org.jboss.hal.ballroom.Alert;
import org.jboss.hal.core.finder.PreviewAttributes;
import org.jboss.hal.core.finder.PreviewContent;
import org.jboss.hal.resources.Icons;
import org.jboss.hal.resources.Resources;

import static java.util.Arrays.asList;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RUNTIME_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STATUS;
import static org.jboss.hal.resources.Names.DEPLOYMENT;

/**
 * @author Harald Pehl
 */
class DeploymentPreview extends PreviewContent<Deployment> {

    DeploymentPreview(final DeploymentColumn column, final Deployment deployment, final Resources resources) {
        super(deployment.getName());

        if (deployment.isEnabled()) {
            previewBuilder()
                    .add(new Alert(Icons.OK, resources.messages().resourceEnabled(DEPLOYMENT, deployment.getName()),
                            resources.constants().disable(), event -> column.disable(deployment)));

        } else {
            previewBuilder()
                    .add(new Alert(Icons.DISABLED,
                            resources.messages().resourceDisabled(DEPLOYMENT, deployment.getName()),
                            resources.constants().enable(), event -> column.enable(deployment)));
        }

        PreviewAttributes<Deployment> attributes = new PreviewAttributes<>(deployment,
                asList(RUNTIME_NAME, "disabled-timestamp", "enabled-timestamp", STATUS)).end();
        previewBuilder().addAll(attributes);
    }
}
