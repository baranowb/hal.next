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
package org.jboss.hal.client.configuration.subsystem.security;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.gwt.flow.Async;
import org.jboss.gwt.flow.Function;
import org.jboss.gwt.flow.FunctionContext;
import org.jboss.gwt.flow.Progress;
import org.jboss.hal.ballroom.form.Form;
import org.jboss.hal.ballroom.table.Api;
import org.jboss.hal.core.CrudOperations;
import org.jboss.hal.core.finder.Finder;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.mbui.MbuiPresenter;
import org.jboss.hal.core.mbui.MbuiView;
import org.jboss.hal.core.mbui.dialog.AddResourceDialog;
import org.jboss.hal.core.mvp.SupportsExpertMode;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.dmr.model.NamedNode;
import org.jboss.hal.dmr.model.Operation;
import org.jboss.hal.dmr.model.ResourceAddress;
import org.jboss.hal.dmr.model.ResourceCheck;
import org.jboss.hal.dmr.model.SuccessfulOutcome;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.Metadata;
import org.jboss.hal.meta.MetadataRegistry;
import org.jboss.hal.meta.SelectionAwareStatementContext;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jboss.hal.spi.Footer;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;
import org.jboss.hal.spi.Requires;

import static org.jboss.hal.client.configuration.subsystem.security.AddressTemplates.SECURITY_DOMAIN_ADDRESS;
import static org.jboss.hal.client.configuration.subsystem.security.AddressTemplates.SECURITY_DOMAIN_TEMPLATE;
import static org.jboss.hal.client.configuration.subsystem.security.AddressTemplates.SELECTED_SECURITY_DOMAIN_TEMPLATE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ADD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SECURITY;
import static org.jboss.hal.meta.token.NameTokens.SECURITY_DOMAIN;

/**
 * @author Harald Pehl
 */
public class SecurityDomainPresenter
        extends MbuiPresenter<SecurityDomainPresenter.MyView, SecurityDomainPresenter.MyProxy>
        implements SupportsExpertMode {

    // @formatter:off
    @ProxyCodeSplit
    @NameToken(SECURITY_DOMAIN)
    @Requires(SECURITY_DOMAIN_ADDRESS)
    public interface MyProxy extends ProxyPlace<SecurityDomainPresenter> {}

    public interface MyView extends MbuiView<SecurityDomainPresenter> {
        void update(SecurityDomain securityDomain);
    }
    // @formatter:on


    private final Dispatcher dispatcher;
    private final CrudOperations crud;
    private final Provider<Progress> progress;
    private final MetadataRegistry metadataRegistry;
    private final FinderPathFactory finderPathFactory;
    private final StatementContext statementContext;
    private final Resources resources;
    private String securityDomain;

    @Inject
    public SecurityDomainPresenter(final EventBus eventBus,
            final SecurityDomainPresenter.MyView view,
            final SecurityDomainPresenter.MyProxy myProxy,
            final Finder finder,
            final Dispatcher dispatcher,
            final CrudOperations crud,
            @Footer final Provider<Progress> progress,
            final MetadataRegistry metadataRegistry,
            final FinderPathFactory finderPathFactory,
            final StatementContext statementContext,
            final Resources resources) {
        super(eventBus, view, myProxy, finder);
        this.dispatcher = dispatcher;
        this.crud = crud;
        this.progress = progress;
        this.metadataRegistry = metadataRegistry;
        this.finderPathFactory = finderPathFactory;
        this.statementContext = new SelectionAwareStatementContext(statementContext, () -> securityDomain);
        this.resources = resources;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(final PlaceRequest request) {
        super.prepareFromRequest(request);
        securityDomain = request.getParameter(NAME, null);
    }

    @Override
    public ResourceAddress resourceAddress() {
        return SELECTED_SECURITY_DOMAIN_TEMPLATE.resolve(statementContext);
    }

    @Override
    public FinderPath finderPath() {
        return finderPathFactory.subsystemPath(SECURITY)
                .append(Ids.SECURITY_DOMAIN, Ids.securityDomain(securityDomain), Names.SECURITY_DOMAIN, securityDomain);
    }

    @Override
    protected void reload() {
        ResourceAddress address = SELECTED_SECURITY_DOMAIN_TEMPLATE.resolve(statementContext);
        crud.readRecursive(address, result -> getView().update(new SecurityDomain(securityDomain, result)));
    }

    void saveSecurityDomain(Form<ModelNode> form, Map<String, Object> changedValues) {
        crud.save(Names.SECURITY_DOMAIN, securityDomain, SELECTED_SECURITY_DOMAIN_TEMPLATE, changedValues,
                this::reload);
    }

    void addClassicAuthenticationModule() {
        // Check if there's already a 'authentication=jaspi' singleton node.
        // Either 'authentication=classic' or 'authentication=jaspi' is allowed not both!
        Operation operation = new Operation.Builder(READ_RESOURCE_OPERATION,
                SELECTED_SECURITY_DOMAIN_TEMPLATE.append("authentication=jaspi").resolve(statementContext))
                .build();
        dispatcher.execute(operation,
                result -> {
                    // error: there's already a 'authentication=jaspi' singleton
                    MessageEvent.fire(getEventBus(), Message.error(resources.messages().duplicateAuthenticationModule(),
                            resources.messages().duplicateAuthenticationModuleReason()));
                },
                (op, failure) -> {
                    // everything ok: no 'authentication=jaspi' found
                    addModule(Module.AUTHENTICATION);
                });
    }

    void addModule(Module module) {
        // first check for (and add) the intermediate singleton
        AddressTemplate singletonTemplate = SELECTED_SECURITY_DOMAIN_TEMPLATE.append(module.singleton);
        Function[] functions = new Function[]{
                new ResourceCheck(dispatcher, singletonTemplate.resolve(statementContext)),
                (Function<FunctionContext>) control -> {
                    int status = control.getContext().pop();
                    if (status == 200) {
                        control.proceed();
                    } else {
                        Operation operation = new Operation.Builder(ADD, singletonTemplate.resolve(statementContext))
                                .build();
                        dispatcher.execute(operation, result -> control.proceed());
                    }
                }
        };

        // then add the final resource
        new Async<FunctionContext>(progress.get()).waterfall(new FunctionContext(),
                new SuccessfulOutcome(getEventBus(), resources) {
                    @Override
                    public void onSuccess(final FunctionContext context) {
                        AddressTemplate metadataTemplate = SECURITY_DOMAIN_TEMPLATE
                                .append(module.singleton)
                                .append(module.resource + "=*");
                        AddressTemplate selectionTemplate = SELECTED_SECURITY_DOMAIN_TEMPLATE
                                .append(module.singleton)
                                .append(module.resource + "=*");
                        Metadata metadata = metadataRegistry.lookup(metadataTemplate);
                        AddResourceDialog dialog = new AddResourceDialog(module.id,
                                resources.messages().addResourceTitle(module.type),
                                metadata,
                                (name, modelNode) -> {
                                    ResourceAddress address = selectionTemplate.resolve(statementContext, name);
                                    crud.add(module.type, name, address, modelNode, (n, a) -> reload());
                                });
                        dialog.show();
                    }
                }, functions);
    }

    void saveModule(Form<NamedNode> form, Map<String, Object> changedValues, Module module) {
        crud.save(module.type,
                form.getModel().getName(),
                SELECTED_SECURITY_DOMAIN_TEMPLATE
                        .append(module.singleton)
                        .append(module.resource + "=*")
                        .resolve(statementContext),
                changedValues,
                this::reload);
    }

    void removeModule(Api<NamedNode> api, Module module) {
        //noinspection ConstantConditions
        String name = api.selectedRow().getName();
        AddressTemplate template = SELECTED_SECURITY_DOMAIN_TEMPLATE
                .append(module.singleton)
                .append(module.resource + "=" + name);
        crud.remove(module.type, name, template.resolve(statementContext), this::reload);
    }
}
