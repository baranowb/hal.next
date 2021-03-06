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
package org.jboss.hal.core.subsystem;

import com.google.gwt.resources.client.ExternalTextResource;

/**
 * @author Harald Pehl
 */
public class SubsystemMetadata {

    private final String name;
    private final String title;
    private final String subtitle;
    private final String token;
    private final String nextColumn;
    private final boolean customImplementation;
    private final ExternalTextResource externalTextResource;

    public SubsystemMetadata(final String name, final String title, final String subtitle, final String token,
            final String nextColumn, final boolean customImplementation) {
        this(name, title, subtitle, token, nextColumn, customImplementation, null);
    }

    public SubsystemMetadata(final String name, final String title, final String subtitle, final String token,
            final String nextColumn, final boolean customImplementation, ExternalTextResource externalTextResource) {
        this.name = name;
        this.title = title;
        this.subtitle = subtitle;
        this.token = token;
        this.nextColumn = nextColumn;
        this.customImplementation = customImplementation;
        this.externalTextResource = externalTextResource;
    }

    @Override
    public String toString() {
        return "Subsystem(" + name + ")";
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getToken() {
        return token;
    }

    public String getNextColumn() {
        return nextColumn;
    }

    public boolean hasCustomImplementation() {
        return customImplementation;
    }

    public ExternalTextResource getExternalTextResource() {
        return externalTextResource;
    }
}
