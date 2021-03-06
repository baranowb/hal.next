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
package org.jboss.hal.meta;

import org.jboss.hal.meta.capabilitiy.Capabilities;
import org.jboss.hal.meta.description.ResourceDescription;
import org.jboss.hal.meta.security.SecurityContext;

/**
 * Simple data struct for common metadata. Only used to keep the method signatures small and tidy.
 *
 * @author Harald Pehl
 */
public class Metadata {

    private final SecurityContext securityContext;
    private final ResourceDescription description;
    private final Capabilities capabilities;

    public Metadata(final SecurityContext securityContext, final ResourceDescription description,
            final Capabilities capabilities) {
        this.securityContext = securityContext;
        this.description = description;
        this.capabilities = capabilities;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public ResourceDescription getDescription() {
        return description;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }
}
