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
package org.jboss.hal.resources;

import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import elemental.dom.Element;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Pehl
 */
public interface Previews extends ClientBundleWithLookup {

    // ------------------------------------------------------ access control (rbac)

    @Source("previews/rbac/administrator.html")
    ExternalTextResource rbacAdministrator();

    @Source("previews/rbac/auditor.html")
    ExternalTextResource rbacAuditor();

    @Source("previews/rbac/deployer.html")
    ExternalTextResource rbacDeployer();

    @Source("previews/rbac/groups.html")
    ExternalTextResource rbacGroups();

    @Source("previews/rbac/maintainer.html")
    ExternalTextResource rbacMaintainer();

    @Source("previews/rbac/monitor.html")
    ExternalTextResource rbacMonitor();

    @Source("previews/rbac/operator.html")
    ExternalTextResource rbacOperator();

    @Source("previews/rbac/overview.html")
    ExternalTextResource rbacOverview();

    @Source("previews/rbac/roles-domain.html")
    ExternalTextResource rbacRolesDomain();

    @Source("previews/rbac/roles-standalone.html")
    ExternalTextResource rbacRolesStandalone();

    @Source("previews/rbac/superuser.html")
    ExternalTextResource rbacSuperUser();

    @Source("previews/rbac/users.html")
    ExternalTextResource rbacUsers();


    // ------------------------------------------------------ configuration

    @Source("previews/configuration/batch.html")
    ExternalTextResource configurationBatch();

    @Source("previews/configuration/datasources.html")
    ExternalTextResource configurationDatasources();

    @Source("previews/configuration/datasources-drivers.html")
    ExternalTextResource configurationDatasourcesDrivers();

    @Source("previews/configuration/deployment-scanner.html")
    ExternalTextResource configurationDeploymentScanner();

    @Source("previews/configuration/domain.html")
    ExternalTextResource configurationDomain();

    @Source("previews/configuration/ee.html")
    ExternalTextResource configurationEe();

    @Source("previews/configuration/interfaces.html")
    ExternalTextResource configurationInterfaces();

    @Source("previews/configuration/io.html")
    ExternalTextResource configurationIo();

    @Source("previews/configuration/jdbc-drivers.html")
    ExternalTextResource configurationJdbcDrivers();

    @Source("previews/configuration/logging.html")
    ExternalTextResource configurationLogging();

    @Source("previews/configuration/logging-configuration.html")
    ExternalTextResource configurationLoggingConfiguration();

    @Source("previews/configuration/logging-profiles.html")
    ExternalTextResource configurationLoggingProfiles();

    @Source("previews/configuration/mail.html")
    ExternalTextResource configurationMail();

    @Source("previews/configuration/paths.html")
    ExternalTextResource configurationPaths();

    @Source("previews/configuration/profiles.html")
    ExternalTextResource configurationProfiles();

    @Source("previews/configuration/socket-bindings.html")
    ExternalTextResource configurationSocketBindings();

    @Source("previews/configuration/standalone.html")
    ExternalTextResource configurationStandalone();

    @Source("previews/configuration/subsystems.html")
    ExternalTextResource configurationSubsystems();

    @Source("previews/configuration/system-properties.html")
    ExternalTextResource configurationSystemProperties();


    // ------------------------------------------------------ deployments

    @Source("previews/deployments/content-repository.html")
    ExternalTextResource deploymentsContentRepository();

    @Source("previews/deployments/domain.html")
    ExternalTextResource deploymentsDomain();

    @Source("previews/deployments/server-groups.html")
    ExternalTextResource deploymentsServerGroups();

    @Source("previews/deployments/standalone.html")
    ExternalTextResource deploymentsStandalone();


    // ------------------------------------------------------ runtime

    @Source("previews/runtime/datasources.html")
    ExternalTextResource runtimeDatasources();

    @Source("previews/runtime/domain.html")
    ExternalTextResource runtimeDomain();

    @Source("previews/runtime/hosts.html")
    ExternalTextResource runtimeHosts();

    @Source("previews/runtime/jndi.html")
    ExternalTextResource runtimeJndi();

    @Source("previews/runtime/jpa.html")
    ExternalTextResource runtimeJpa();

    @Source("previews/runtime/logfiles.html")
    ExternalTextResource runtimeLogFiles();

    @Source("previews/runtime/server-groups.html")
    ExternalTextResource runtimeServerGroups();

    @Source("previews/runtime/standalone.html")
    ExternalTextResource runtimeStandalone();

    @Source("previews/runtime/subsystems.html")
    ExternalTextResource runtimeSubsystems();

    @Source("previews/runtime/topology.html")
    ExternalTextResource runtimeTopology();


    // ------------------------------------------------------ helper methods

    @NonNls Logger logger = LoggerFactory.getLogger(Previews.class);

    @SuppressWarnings("DuplicateStringLiteralInspection")
    static void innerHtml(Element element, ExternalTextResource resource) {
        if (resource != null) {
            try {
                resource.getText(new ResourceCallback<TextResource>() {
                    @Override
                    public void onError(final ResourceException e) {
                        logger.error("Unable to get preview content from '{}': {}", resource.getName(), e.getMessage());
                    }

                    @Override
                    public void onSuccess(final TextResource textResource) {
                        SafeHtml html = SafeHtmlUtils.fromSafeConstant(textResource.getText());
                        element.setInnerHTML(html.asString());
                    }
                });
            } catch (ResourceException e) {
                logger.error("Unable to get preview content from '{}': {}", resource.getName(), e.getMessage());
            }
        }
    }
}
