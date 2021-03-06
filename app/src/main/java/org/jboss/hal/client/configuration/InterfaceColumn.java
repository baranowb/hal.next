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
package org.jboss.hal.client.configuration;

import java.util.List;
import javax.inject.Inject;

import org.jboss.hal.core.finder.ColumnActionFactory;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderColumn;
import org.jboss.hal.core.finder.ItemAction;
import org.jboss.hal.core.finder.ItemActionFactory;
import org.jboss.hal.core.finder.ItemDisplay;
import org.jboss.hal.dmr.Property;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.dmr.model.Operation;
import org.jboss.hal.dmr.model.ResourceAddress;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.spi.Column;
import org.jboss.hal.spi.Requires;

import static java.util.Arrays.asList;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CHILD_TYPE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.INTERFACE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION;

@Column(Ids.INTERFACE)
@Requires(InterfacePresenter.ROOT_ADDRESS)
public class InterfaceColumn extends FinderColumn<Property> {

    @Inject
    public InterfaceColumn(final Finder finder,
            final ColumnActionFactory columnActionFactory,
            final ItemActionFactory itemActionFactory,
            final Dispatcher dispatcher) {

        super(new Builder<Property>(finder, Ids.INTERFACE, Names.INTERFACE)

                .columnAction(columnActionFactory.add(
                        Ids.INTERFACE_ADD,
                        Names.INTERFACE,
                        InterfacePresenter.ROOT_TEMPLATE,
                        "inet-address"))
                .columnAction(columnActionFactory.refresh(Ids.INTERFACE_REFRESH))

                .itemsProvider((context, callback) -> {
                    Operation operation = new Operation.Builder(READ_CHILDREN_RESOURCES_OPERATION, ResourceAddress.ROOT)
                            .param(CHILD_TYPE, INTERFACE).build();
                    dispatcher.execute(operation, result -> {
                        callback.onSuccess(result.asPropertyList());
                    });
                })

                .useFirstActionAsBreadcrumbHandler());

        setItemRenderer(property -> new ItemDisplay<Property>() {
            @Override
            public String getTitle() {
                return property.getName();
            }

            @Override
            public List<ItemAction<Property>> actions() {
                return asList(
                        itemActionFactory.view(NameTokens.INTERFACE, NAME, property.getName()),
                        itemActionFactory.remove(Names.INTERFACE, property.getName(), InterfacePresenter.ROOT_TEMPLATE,
                                InterfaceColumn.this));
            }
        });
    }
}
