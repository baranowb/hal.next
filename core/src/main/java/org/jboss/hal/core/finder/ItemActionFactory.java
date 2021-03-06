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
package org.jboss.hal.core.finder;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.hal.ballroom.dialog.DialogFactory;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.dmr.model.Operation;
import org.jboss.hal.dmr.model.ResourceAddress;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;

import static org.jboss.hal.core.finder.FinderColumn.RefreshMode.CLEAR_SELECTION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.REMOVE;

/**
 * Convenience methods for common item actions.
 *
 * @author Harald Pehl
 */
public class ItemActionFactory {

    private final ItemMonitor itemMonitor;
    private final StatementContext statementContext;
    private final Dispatcher dispatcher;
    private final EventBus eventBus;
    private final PlaceManager placeManager;
    private final Resources resources;

    @Inject
    public ItemActionFactory(ItemMonitor itemMonitor,
            StatementContext statementContext,
            Dispatcher dispatcher,
            EventBus eventBus,
            PlaceManager placeManager,
            Resources resources) {
        this.itemMonitor = itemMonitor;
        this.statementContext = statementContext;
        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
        this.placeManager = placeManager;
        this.resources = resources;
    }

    public <T> ItemAction<T> view(String nameToken, String... parameter) {
        PlaceRequest.Builder builder = new PlaceRequest.Builder().nameToken(nameToken);
        if (parameter != null && parameter.length > 1) {
            if (parameter.length % 2 != 0) {
                throw new IllegalArgumentException(
                        "Parameter in ItemActionFactory.action('" + nameToken + "') must be key/value pairs");
            }
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < parameter.length; i += 2) {
                map.put(parameter[i], parameter[i + 1]);
            }
            builder.with(map);
        }
        return view(builder.build());
    }

    public <T> ItemAction<T> view(PlaceRequest placeRequest) {
        return new ItemAction<>(resources.constants().view(), item -> placeManager.revealPlace(placeRequest));
    }

    public <T> ItemAction<T> viewAndMonitor(String itemId, PlaceRequest placeRequest) {
        return new ItemAction<>(resources.constants().view(),
                itemMonitor.monitorPlaceRequest(itemId, placeRequest.getNameToken(),
                        () -> placeManager.revealPlace(placeRequest)));
    }

    /**
     * Wraps the specified handler inside a confirmation dialog. The action is executed when upon confirmation.
     */
    public <T> ItemAction<T> remove(String type, String name, ItemActionHandler<T> handler) {
        return new ItemAction<>(resources.constants().remove(), item -> DialogFactory.showConfirmation(
                resources.messages().removeResourceConfirmationTitle(type),
                resources.messages().removeResourceConfirmationQuestion(name),
                () -> handler.execute(item)));
    }

    /**
     * Creates a 'remove' action which removes the specified resource from the given address. The address can contain a
     * wildcard which is replaced by the resource name. The action wil bring up a confirmation dialog. If confirmed the
     * resource is removed and {@link FinderColumn#refresh(FinderColumn.RefreshMode)} is called.
     */
    public <T> ItemAction<T> remove(String type, String name, AddressTemplate addressTemplate, FinderColumn<T> column) {
        return new ItemAction<>(resources.constants().remove(), item -> DialogFactory.showConfirmation(
                resources.messages().removeResourceConfirmationTitle(type),
                resources.messages().removeResourceConfirmationQuestion(name),
                () -> {
                    ResourceAddress address = addressTemplate.resolve(statementContext, name);
                    Operation operation = new Operation.Builder(REMOVE, address).build();
                    dispatcher.execute(operation, result -> {
                        MessageEvent.fire(eventBus,
                                Message.success(resources.messages().removeResourceSuccess(type, name)));
                        column.refresh(CLEAR_SELECTION);
                    });
                }));
    }
}
