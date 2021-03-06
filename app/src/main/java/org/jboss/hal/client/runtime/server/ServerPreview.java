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
package org.jboss.hal.client.runtime.server;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import elemental.dom.Element;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.hal.client.runtime.RuntimePreview;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.PreviewAttributes;
import org.jboss.hal.core.mvp.Places;
import org.jboss.hal.core.runtime.server.Server;
import org.jboss.hal.core.runtime.server.ServerActions;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Icons;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;

import static java.util.Arrays.asList;
import static org.jboss.gwt.elemento.core.EventType.click;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.resources.CSS.alert;
import static org.jboss.hal.resources.CSS.alertInfo;
import static org.jboss.hal.resources.CSS.alertLink;
import static org.jboss.hal.resources.CSS.clickable;

/**
 * @author Harald Pehl
 */
class ServerPreview extends RuntimePreview<Server> {

    private static final String START_LINK = "start-link";
    private static final String STOP_LINK = "stop-link";
    private static final String RESUME_LINK = "resume-link";

    private final ServerActions serverActions;
    private final Element startLink;
    private final Element stopLink;
    private final Element reloadLink;
    private final Element restartLink;
    private final Element resumeLink;
    private final PreviewAttributes<Server> attributes;

    ServerPreview(final ServerActions serverActions, final Server server, final Places places,
            final Resources resources) {
        super(server.getName(), null, resources);
        this.serverActions = serverActions;

        // @formatter:off
        previewBuilder()
            .div().rememberAs(ALERT_CONTAINER)
                .span().rememberAs(ALERT_ICON).end()
                .span().rememberAs(ALERT_TEXT).end()
                .span().textContent(" ").end()
                .a().rememberAs(START_LINK).css(clickable, alertLink)
                    .on(click, event -> serverActions.start(server))
                    .textContent(resources.constants().start())
                .end()
                .a().rememberAs(STOP_LINK).css(clickable, alertLink)
                    .on(click, event -> serverActions.stop(server))
                    .textContent(resources.constants().stop())
                .end()
                .a().rememberAs(RELOAD_LINK).css(clickable, alertLink)
                    .on(click, event -> serverActions.reload(server))
                    .textContent(resources.constants().reload())
                .end()
                .a().rememberAs(RESTART_LINK).css(clickable, alertLink)
                    .on(click, event -> serverActions.restart(server))
                    .textContent(resources.constants().restart())
                .end()
                .a().rememberAs(RESUME_LINK).css(clickable, alertLink)
                    .on(click, event -> serverActions.resume(server))
                    .textContent(resources.constants().resume())
                .end()
            .end();
        // @formatter:on

        alertContainer = previewBuilder().referenceFor(ALERT_CONTAINER);
        alertIcon = previewBuilder().referenceFor(ALERT_ICON);
        alertText = previewBuilder().referenceFor(ALERT_TEXT);
        startLink = previewBuilder().referenceFor(START_LINK);
        stopLink = previewBuilder().referenceFor(STOP_LINK);
        reloadLink = previewBuilder().referenceFor(RELOAD_LINK);
        restartLink = previewBuilder().referenceFor(RESTART_LINK);
        resumeLink = previewBuilder().referenceFor(RESUME_LINK);

        if (server.isStandalone()) {
            this.attributes = new PreviewAttributes<>(server, asList(STATUS, RUNNING_MODE, SERVER_STATE, SUSPEND_STATE))
                    .end();
        } else {
            PlaceRequest profilePlaceRequest = places
                    .finderPlace(NameTokens.CONFIGURATION, new FinderPath()
                            .append(Ids.CONFIGURATION, Ids.asId(Names.PROFILES))
                            .append(Ids.PROFILE, server.get(PROFILE_NAME).asString()))
                    .build();
            String profileHref = places.historyToken(profilePlaceRequest);
            this.attributes = new PreviewAttributes<>(server)
                    .append(HOST)
                    .append(GROUP)
                    .append(PROFILE_NAME, profileHref)
                    .append(AUTO_START)
                    .append(SOCKET_BINDING_PORT_OFFSET)
                    .append(STATUS)
                    .append(RUNNING_MODE)
                    .append(SERVER_STATE)
                    .append(SUSPEND_STATE)
                    .end();
        }
        previewBuilder().addAll(this.attributes);

        update(server);
    }

    @Override
    public void update(final Server server) {
        boolean pending = serverActions.isPending(server);
        if (pending) {
            pending(resources.messages().serverPending(server.getName()));
        } else if (server.isAdminMode()) {
            adminOnly(resources.messages().serverAdminMode(server.getName()));
        } else if (server.isStarting()) {
            starting(resources.messages().serverStarting(server.getName()));
        } else if (server.isSuspended()) {
            suspended(resources.messages().serverSuspended(server.getName()));
        } else if (server.needsReload()) {
            needsReload(resources.messages().serverNeedsReload(server.getName()));
        } else if (server.needsRestart()) {
            needsRestart(resources.messages().serverNeedsRestart(server.getName()));
        } else if (server.isRunning()) {
            running(resources.messages().serverRunning(server.getName()));
        } else if (server.isFailed()) {
            timeout(resources.messages().serverFailed(server.getName()));
        } else if (server.isStopped()) {
            alertContainer.setClassName(alert + " " + alertInfo);
            alertIcon.setClassName(Icons.STOPPED);
            alertText.setInnerHTML(resources.messages().serverStopped(server.getName()).asString());
        } else {
            unknown(resources.messages().serverUndefined(server.getName()));
        }

        if (pending) {
            disableAllLinks();
        } else if (server.isSuspended()) {
            Elements.setVisible(startLink, false);
            Elements.setVisible(stopLink, false);
            Elements.setVisible(reloadLink, false);
            Elements.setVisible(restartLink, false);
            Elements.setVisible(resumeLink, server.isSuspended());
        } else if (server.needsReload() || server.needsRestart()) {
            Elements.setVisible(startLink, false);
            Elements.setVisible(stopLink, false);
            Elements.setVisible(reloadLink, server.needsReload());
            Elements.setVisible(restartLink, server.needsRestart());
            Elements.setVisible(resumeLink, false);
        } else if (server.isStopped() || server.isFailed()) {
            Elements.setVisible(startLink, !server.isStandalone());
            Elements.setVisible(stopLink, false);
            Elements.setVisible(reloadLink, false);
            Elements.setVisible(restartLink, false);
            Elements.setVisible(resumeLink, false);
        } else if (server.isRunning()) {
            Elements.setVisible(startLink, false);
            Elements.setVisible(stopLink, !server.isStandalone());
            Elements.setVisible(reloadLink, false);
            Elements.setVisible(restartLink, false);
            Elements.setVisible(resumeLink, false);
        } else {
            disableAllLinks();
        }
        attributes.setVisible(PROFILE_NAME, server.isStarted());
        attributes.setVisible(RUNNING_MODE, server.isStarted());
        attributes.setVisible(SERVER_STATE, server.isStarted());
        attributes.setVisible(SUSPEND_STATE, server.isStarted());
    }

    private void disableAllLinks() {
        Elements.setVisible(startLink, false);
        Elements.setVisible(stopLink, false);
        Elements.setVisible(reloadLink, false);
        Elements.setVisible(restartLink, false);
        Elements.setVisible(resumeLink, false);
    }
}
