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
package org.jboss.hal.client.accesscontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.collect.Sets;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.web.bindery.event.shared.EventBus;
import elemental.client.Browser;
import elemental.dom.Element;
import org.jboss.gwt.flow.Async;
import org.jboss.gwt.flow.FunctionContext;
import org.jboss.gwt.flow.Progress;
import org.jboss.hal.client.accesscontrol.AccessControlFunctions.AddAssignment;
import org.jboss.hal.client.accesscontrol.AccessControlFunctions.AddRoleMapping;
import org.jboss.hal.client.accesscontrol.AccessControlFunctions.CheckRoleMapping;
import org.jboss.hal.core.finder.ColumnAction;
import org.jboss.hal.core.finder.ColumnActionHandler;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderColumn;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderSegment;
import org.jboss.hal.core.finder.ItemAction;
import org.jboss.hal.core.finder.ItemActionFactory;
import org.jboss.hal.core.finder.ItemDisplay;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.dmr.model.Operation;
import org.jboss.hal.dmr.model.ResourceAddress;
import org.jboss.hal.dmr.model.SuccessfulOutcome;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.AsyncColumn;
import org.jboss.hal.spi.Footer;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.jboss.hal.dmr.ModelDescriptionConstants.REMOVE;
import static org.jboss.hal.resources.CSS.fontAwesome;

/**
 * Shows the assigned roles of the selected principal (the reverse of the {@link MembershipColumn}.
 *
 * @author Harald Pehl
 */
@AsyncColumn(Ids.ASSIGNMENT)
public class AssignmentColumn extends FinderColumn<Assignment> {

    private final Dispatcher dispatcher;
    private final EventBus eventBus;
    private final Provider<Progress> progress;
    private final AccessControl accessControl;
    private final Resources resources;

    @Inject
    public AssignmentColumn(final Finder finder,
            final ItemActionFactory itemActionFactory,
            final Dispatcher dispatcher,
            final EventBus eventBus,
            final @Footer Provider<Progress> progress,
            final AccessControl accessControl,
            final AccessControlTokens tokens,
            final Resources resources) {

        super(new Builder<Assignment>(finder, Ids.ASSIGNMENT, resources.constants().assignment())
                .withFilter()
                .onPreview(item -> new AssignmentPreview(tokens, item.getRole(), resources)));
        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
        this.progress = progress;
        this.accessControl = accessControl;
        this.resources = resources;

        setItemsProvider((context, callback) -> {
            List<Assignment> assignments = new ArrayList<>();
            Principal principal = findPrincipal(finder.getContext().getPath());
            if (principal != null) {
                accessControl.assignments().byPrincipal(principal)
                        .sorted(Assignments.EXCLUDES_FIRST
                                .thenComparing(Assignments.STANDARD_FIRST)
                                .thenComparing(Assignments.BY_ROLE_NAME))
                        .forEach(assignments::add);
            }

            // Add the missing roles to the include / exclude column action drop-downs
            resetColumnActions();
            Set<Role> assignedRoles = assignments.stream().map(Assignment::getRole).collect(toSet());
            Set<Role> missingRoles = Sets.newHashSet(accessControl.roles());
            missingRoles.removeAll(assignedRoles);

            List<ColumnAction<Assignment>> includeActions = missingRoles.stream()
                    .map(role -> new ColumnAction<>(includeId(role), role.getName(), columnActionHandler(role, true)))
                    .collect(toList());
            if (!includeActions.isEmpty()) {
                addColumnActions(Ids.ASSIGNMENT_INCLUDE, fontAwesome("plus"), resources.constants().includeRole(),
                        includeActions);
            }

            List<ColumnAction<Assignment>> excludeActions = missingRoles.stream()
                    .map(role -> new ColumnAction<>(excludeId(role), role.getName(), columnActionHandler(role, false)))
                    .collect(toList());
            if (!excludeActions.isEmpty()) {
                addColumnActions(Ids.ASSIGNMENT_EXCLUDE, fontAwesome("minus"), resources.constants().excludeRole(),
                        excludeActions);
            }

            callback.onSuccess(assignments);
        });

        setItemRenderer(item -> new ItemDisplay<Assignment>() {
            @Override
            public String getId() {
                return item.getRole().getId();
            }

            @Override
            public String getTitle() {
                return item.getRole().getName();
            }

            @Override
            public Element asElement() {
                if (item.getRole().isScoped()) {
                    return ItemDisplay.withSubtitle(item.getRole().getName(),
                            item.getRole().getBaseRole().getName() + " / " +
                                    String.join(", ", item.getRole().getScope()));
                }
                return null;
            }

            @Override
            public String getTooltip() {
                return item.isInclude() ? resources.constants().includes() : resources.constants().excludes();
            }

            @Override
            public Element getIcon() {
                Element icon = Browser.getDocument().createSpanElement();
                if (item.isInclude()) {
                    icon.setClassName(fontAwesome("plus"));
                } else {
                    icon.setClassName(fontAwesome("minus"));
                }
                return icon;
            }

            @Override
            public String getFilterData() {
                List<String> data = new ArrayList<>(RoleColumn.filterData(item.getRole()));
                data.add(item.isInclude() ? "includes" : "excludes"); //NON-NLS
                return String.join(" ", data);
            }

            @Override
            public List<ItemAction<Assignment>> actions() {
                return singletonList(itemActionFactory.remove(resources.constants().assignment(),
                        item.getRole().getName(), itm -> {
                            ResourceAddress address = AddressTemplates.assignment(itm);
                            Operation operation = new Operation.Builder(REMOVE, address).build();
                            dispatcher.execute(operation, result -> {
                                MessageEvent.fire(eventBus, Message.success(resources.messages()
                                        .removeResourceSuccess(resources.constants().assignment(),
                                                itm.getRole().getName())));
                                accessControl.reload(() -> refresh(RefreshMode.CLEAR_SELECTION));
                            });
                        }));
            }
        });
    }

    private Principal findPrincipal(FinderPath path) {
        Optional<String> optional = StreamSupport.stream(path.spliterator(), false)
                .filter(segment -> Ids.USER.equals(segment.getColumnId()) || Ids.GROUP.equals(segment.getColumnId()))
                .findFirst()
                .map(FinderSegment::getItemId);
        return optional.isPresent() ? accessControl.principals().get(optional.get()) : null;
    }

    private String includeId(Role role) {
        return Ids.build(Ids.ASSIGNMENT_INCLUDE, role.getName());
    }

    private String excludeId(Role role) {
        return Ids.build(Ids.ASSIGNMENT_EXCLUDE, role.getName());
    }

    private ColumnActionHandler<Assignment> columnActionHandler(Role role, boolean include) {
        return column -> {
            Principal principal = findPrincipal(getFinder().getContext().getPath());
            if (principal != null) {
                new Async<FunctionContext>(progress.get()).waterfall(new FunctionContext(),
                        new SuccessfulOutcome(eventBus, resources) {
                            @Override
                            public void onSuccess(final FunctionContext context) {
                                String type = resources.constants().role();
                                SafeHtml message = include
                                        ? resources.messages().assignmentIncludeSuccess(type, role.getName())
                                        : resources.messages().assignmentExcludeSuccess(type, role.getName());
                                MessageEvent.fire(eventBus, Message.success(message));
                                accessControl.reload(() -> refresh(RefreshMode.RESTORE_SELECTION));
                            }
                        },
                        new CheckRoleMapping(dispatcher, role),
                        new AddRoleMapping(dispatcher, role, status -> status == 404),
                        new AddAssignment(dispatcher, role, principal, include));
            }
        };
    }
}
