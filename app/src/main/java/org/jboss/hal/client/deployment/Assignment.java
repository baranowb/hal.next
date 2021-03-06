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

import org.jboss.hal.dmr.ModelNode;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DISABLED;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ENABLED;

/**
 * An assigned deployment used in domain mode.
 *
 * @author Harald Pehl
 */
public class Assignment extends Content {

    private final String serverGroup;
    private Deployment deployment; // might be null if there's no reference server available

    public Assignment(final String serverGroup, final ModelNode node) {
        super(node);
        this.serverGroup = serverGroup;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Assignment)) { return false; }
        if (!super.equals(o)) { return false; }

        Assignment that = (Assignment) o;
        //noinspection SimplifiableIfStatement
        if (!serverGroup.equals(that.serverGroup)) { return false; }
        return getName().equals(that.getName());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + serverGroup.hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }

    public boolean isEnabled() {
        ModelNode enabled = get(ENABLED);
        //noinspection SimplifiableConditionalExpression
        return enabled.isDefined() ? enabled.asBoolean() : false;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public boolean hasDeployment() {
        return deployment != null;
    }

    public void setDeployment(final Deployment deployment) {
        this.deployment = deployment;
    }

    @Override
    public String toString() {
        return "Assignment{" + getName() + "@" + serverGroup + ", " + (isEnabled() ? ENABLED : DISABLED) + "}";
    }
}
