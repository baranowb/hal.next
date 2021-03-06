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
package org.jboss.hal.core.mbui.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.gwt.flow.Progress;
import org.jboss.hal.ballroom.dialog.DialogFactory;
import org.jboss.hal.ballroom.table.Api;
import org.jboss.hal.ballroom.table.Button;
import org.jboss.hal.core.mbui.dialog.AddResourceDialog;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.dmr.model.Operation;
import org.jboss.hal.dmr.model.ResourceAddress;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.Metadata;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.meta.processing.MetadataProcessor;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.Footer;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;
import org.jetbrains.annotations.NonNls;

import static java.util.Arrays.asList;
import static org.jboss.hal.ballroom.table.Button.Scope.SELECTED_SINGLE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ADD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.REMOVE;

/**
 * @author Harald Pehl
 */
public class TableButtonFactory {

    private final MetadataProcessor metadataProcessor;
    private final Provider<Progress> progress;
    private final Dispatcher dispatcher;
    private final EventBus eventBus;
    private final Resources resources;
    private StatementContext statementContext;

    @Inject
    public TableButtonFactory(final MetadataProcessor metadataProcessor,
            @Footer final Provider<Progress> progress,
            final Dispatcher dispatcher,
            final EventBus eventBus,
            final StatementContext statementContext,
            final Resources resources) {
        this.metadataProcessor = metadataProcessor;
        this.progress = progress;
        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
        this.statementContext = statementContext;
        this.resources = resources;
    }

    public <T extends ModelNode> Button<T> add(String id, String type, AddressTemplate template,
            ScheduledCommand afterAdd) {
        return add(id, type, template, afterAdd, null);
    }

    public <T extends ModelNode> Button<T> add(String id, String type, AddressTemplate template,
            ScheduledCommand afterAdd, @NonNls final String firstAttribute, @NonNls final String... otherAttributes) {

        AddResourceDialog.Callback addResourceCallback = (name, model) -> {
            ResourceAddress address = template.resolve(statementContext, name);
            Operation operation = new Operation.Builder(ADD, address)
                    .payload(model)
                    .build();
            dispatcher.execute(operation, result -> {
                MessageEvent.fire(eventBus,
                        Message.success(resources.messages().addResourceSuccess(type, name)));
                if (afterAdd != null) {
                    afterAdd.execute();
                }
            });
        };

        MetadataProcessor.MetadataCallback metadataCallback = new MetadataProcessor.MetadataCallback() {
            @Override
            public void onError(final Throwable error) {
                MessageEvent.fire(eventBus,
                        Message.error(resources.messages().metadataError(), error.getMessage()));
            }

            @Override
            public void onMetadata(final Metadata metadata) {
                List<String> attributes = new ArrayList<>();
                if (firstAttribute != null) {
                    attributes.add(firstAttribute);
                }
                if (otherAttributes != null) {
                    attributes.addAll(asList(otherAttributes));
                }
                AddResourceDialog dialog = new AddResourceDialog(Ids.build(id, Ids.FORM_SUFFIX),
                        resources.messages().addResourceTitle(type), metadata, attributes, addResourceCallback);
                dialog.show();
            }
        };

        Button<T> button = new Button<>();
        button.text = resources.constants().add();
        button.action = (event, api) -> metadataProcessor.lookup(template, progress.get(), metadataCallback);
        return button;
    }

    public <T> Button<T> remove(String type, AddressTemplate addressTemplate, Function<Api<T>, String> nameFunction,
            ScheduledCommand afterRemove) {
        Button<T> button = new Button<>();
        button.text = resources.constants().remove();
        button.extend = SELECTED_SINGLE.selector();
        button.action = (event, api) ->
                DialogFactory.showConfirmation(
                        resources.messages().removeResourceConfirmationTitle(type),
                        resources.messages().removeResourceConfirmationQuestion(nameFunction.apply(api)),
                        () -> {
                            ResourceAddress address = addressTemplate
                                    .resolve(statementContext, nameFunction.apply(api));
                            Operation operation = new Operation.Builder(REMOVE, address).build();
                            dispatcher.execute(operation, result -> {
                                MessageEvent.fire(eventBus, Message.success(
                                        resources.messages().removeResourceSuccess(type, nameFunction.apply(api))));
                                if (afterRemove != null) {
                                    afterRemove.execute();
                                }
                            });
                        });
        return button;
    }
}
