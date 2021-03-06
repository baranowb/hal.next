<#-- @ftlvariable name="context" type="org.jboss.hal.processor.mbui.MbuiViewContext" -->
package ${context.package};

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import elemental.dom.Element;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.gwt.elemento.core.TemplateUtil;
<#if context.hasTabs()??>
import org.jboss.hal.ballroom.Tabs;
</#if>
import org.jboss.hal.ballroom.table.Button;
import org.jboss.hal.ballroom.table.Options;
import org.jboss.hal.ballroom.LayoutBuilder;
import org.jboss.hal.ballroom.typeahead.Typeahead;
<#if context.verticalNavigation??>
import org.jboss.hal.ballroom.VerticalNavigation;
</#if>
import org.jboss.hal.core.mbui.MbuiContext;
import org.jboss.hal.core.mbui.dialog.AddResourceDialog;
import org.jboss.hal.core.mbui.form.ModelNodeForm;
import org.jboss.hal.core.mbui.table.ModelNodeTable;
import org.jboss.hal.dmr.model.Operation;
import org.jboss.hal.dmr.model.ResourceAddress;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.Metadata;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.spi.Message;
import org.jboss.hal.spi.MessageEvent;

import static java.util.Arrays.asList;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ADD;

/*
 * WARNING! This class is generated. Do not modify.
 */
@Generated("org.jboss.hal.processor.mbui.MbuiViewProcessor")
final class ${context.subclass} extends ${context.base} {

    <#list context.abstractProperties as abstractProperty>
    private final ${abstractProperty.type} ${abstractProperty.field};
    </#list>
    <#list context.metadataInfos as metadataInfo>
    private final Metadata ${metadataInfo.name};
    </#list>
    <#list context.tabs as tabInfo>
    private final Tabs ${tabInfo.name};
    </#list>
    private final Map<String, Element> handlebarElements;

    @SuppressWarnings("unchecked")
    ${context.subclass}(MbuiContext mbuiContext<#list context.abstractProperties as abstractProperty>, ${abstractProperty.type} ${abstractProperty.field}</#list>) {
        super(mbuiContext);

        <#list context.abstractProperties as abstractProperty>
        this.${abstractProperty.field} = ${abstractProperty.field};
        </#list>
        <#list context.metadataInfos as metadataInfo>
        AddressTemplate ${metadataInfo.name}Template = AddressTemplate.of("${metadataInfo.template}");
        this.${metadataInfo.name} = mbuiContext.metadataRegistry().lookup(${metadataInfo.name}Template);
        </#list>
        <#list context.tabs as tabInfo>
        ${tabInfo.name} = new Tabs();
        </#list>
        this.handlebarElements = new HashMap<>();

        <#list context.forms as form>
        ${form.name} = new ModelNodeForm.Builder<${form.typeParameter}>("${form.selector}", ${form.metadata.name})
            <#if form.includeRuntime>
            .includeRuntime()
            </#if>
            <#if form.attributes?has_content>
                <#if form.hasAttributesWithProvider>
                    <#list form.attributes as attribute>
                        <#if attribute.provider??>
            .customFormItem("${attribute.name}", ${attribute.provider})
                        <#else>
            .include("${attribute.name}")
                        </#if>
                    </#list>
                <#else>
            .include(<#list form.attributes as attribute>"${attribute.name}"<#if attribute_has_next>, </#if></#list>)
                </#if>
            .unsorted()
            </#if>
            <#if form.autoSave>
                <#if form.nameResolver??>
            .onSave((form, changedValues) -> {
                String name = ${form.nameResolver};
                saveForm(changedValues, ${form.metadata.name}Template.resolve(mbuiContext.statementContext(), name),
                    ${form.title}, name);
            })
                <#else>
            .onSave((form, changedValues) -> saveSingletonForm(changedValues, ${form.metadata.name}Template.resolve(mbuiContext.statementContext()), ${form.title}))
                </#if>
            <#elseif form.onSave??>
            .onSave((form, changedValues) -> ${form.onSave})
            </#if>
            .build();
            <#list form.validationHandlerAttributes as attribute>
        ${form.name}.getFormItem("${attribute.name}").addValidationHandler(${attribute.validationHandler});
            </#list>
            <#list form.suggestHandlerAttributes as attribute>
                <#if attribute.suggestHandler??>
        ${form.name}.getFormItem("${attribute.name}").registerSuggestHandler(${attribute.suggestHandler});
                <#elseif attribute.suggestHandlerTemplates?size == 1>
        ${form.name}.getFormItem("${attribute.name}").registerSuggestHandler(new Typeahead(
            AddressTemplate.of("${attribute.suggestHandlerTemplates[0]}"), mbuiContext.statementContext()));
                <#else>
        List<AddressTemplate> ${form.name}Templates = asList(<#list attribute.suggestHandlerTemplates as template>
            AddressTemplate.of("${template}")<#if template_has_next>, </#if></#list>);
        ${form.name}.getFormItem("${attribute.name}").registerSuggestHandler(new Typeahead(
            ${form.name}Templates, mbuiContext.statementContext()));
                </#if>
            </#list>
        </#list>

        <#list context.dataTables as table>
        Options<${table.typeParameter}> ${table.name}Options = new ModelNodeTable.Builder<${table.typeParameter}>(${table.metadata.name})
            <#list table.actions as action>
                <#if action.knownHandler>
                    <#switch action.handlerRef>
                        <#case "ADD_RESOURCE">
                            <#if action.attributes?has_content>
                                <#if action.hasAttributesWithProvider>
            .button(mbuiContext.resources().constants().add(), (event, api) -> {
                ModelNodeForm form = new ModelNodeForm.Builder(Ids.build("${table.selector}", Ids.ADD_SUFFIX),
                    ${table.metadata.name})
                    .addFromRequestProperties()
                    .unboundFormItem(new org.jboss.hal.core.mbui.dialog.NameItem(), 0)
                                    <#list action.attributes as attribute>
                                        <#if attribute.provider??>
                    .customFormItem("${attribute.name}", ${attribute.provider})
                                        <#else>
                    .include("${attribute.name}")
                                        </#if>
                                    </#list>
                    .unsorted()
                    .build();
                                    <#list action.validationHandlerAttributes as attribute>
                form.getFormItem("${attribute.name}").addValidationHandler(${attribute.validationHandler});
                                    </#list>
                                    <#list action.suggestHandlerAttributes as attribute>
                                        <#if attribute.suggestHandler??>
                form.getFormItem("${attribute.name}").registerSuggestHandler(${attribute.suggestHandler});
                                        <#elseif attribute.suggestHandlerTemplates?size == 1>
                form.getFormItem("${attribute.name}").registerSuggestHandler(new Typeahead(
                    AddressTemplate.of("${attribute.suggestHandlerTemplates[0]}", mbuiContext.statementContext()));
                                        <#else>
                List<AddressTemplate> ${table.name}Templates = asList(<#list attribute.suggestHandlerTemplates as template>
                    AddressTemplate.of("${template}")<#if template_has_next>, </#if></#list>);
                form.getFormItem("${attribute.name}").registerSuggestHandler(new Typeahead(
                    ${table.name}Templates, mbuiContext.statementContext()));
                                        </#if>
                                    </#list>
                AddResourceDialog dialog = new AddResourceDialog(
                    mbuiContext.resources().messages().addResourceTitle(${table.title}),
                    form,
                    (name, modelNode) -> {
                        ResourceAddress address = ${table.metadata.name}Template.resolve(mbuiContext.statementContext(), name);
                        Operation operation = new Operation.Builder(ADD, address).payload(modelNode).build();
                        mbuiContext.dispatcher().execute(operation, result -> {
                            presenter.reload();
                            MessageEvent.fire(mbuiContext.eventBus(), Message.success(
                            mbuiContext.resources().messages().addResourceSuccess(${table.title}, name)));
                        });
                    });
                dialog.show();
            })
                                <#elseif action.hasAttributesWithValidationsHandler || action.hasAttributesWithSuggestionHandler>
            .button(mbuiContext.resources().constants().add(), (event, api) -> {
                AddResourceDialog dialog = new AddResourceDialog(
                    Ids.build("${table.selector}", Ids.ADD_SUFFIX),
                    mbuiContext.resources().messages().addResourceTitle(${table.title}),
                    ${table.metadata.name},
                    asList(<#list action.attributes as attribute>"${attribute.name}"<#if attribute_has_next>, </#if></#list>),
                    (name, modelNode) -> {
                        ResourceAddress address = ${table.metadata.name}Template.resolve(mbuiContext.statementContext(), name);
                        Operation operation = new Operation.Builder(ADD, address).payload(modelNode).build();
                        mbuiContext.dispatcher().execute(operation, result -> {
                            presenter.reload();
                            MessageEvent.fire(mbuiContext.eventBus(), Message.success(
                                mbuiContext.resources().messages().addResourceSuccess(${table.title}, name)));
                        });
                    });
                                    <#list action.validationHandlerAttributes as attribute>
                dialog.getForm().getFormItem("${attribute.name}").addValidationHandler(${attribute.validationHandler});
                                    </#list>
                                    <#list action.suggestHandlerAttributes as attribute>
                                        <#if attribute.suggestHandler??>
                dialog.getForm().getFormItem("${attribute.name}").registerSuggestHandler(${attribute.suggestHandler});
                                        <#elseif attribute.suggestHandlerTemplates?size == 1>
                dialog.getForm().getFormItem("${attribute.name}").registerSuggestHandler(new Typeahead(
                    AddressTemplate.of("${attribute.suggestHandlerTemplates[0]}"), mbuiContext.statementContext()));
                                        <#else>
                List<AddressTemplate> ${table.name}Templates = asList(<#list attribute.suggestHandlerTemplates as template>
                    AddressTemplate.of("${template}")<#if template_has_next>, </#if></#list>);
                dialog.getForm().getFormItem("${attribute.name}").registerSuggestHandler(new Typeahead(
                    ${table.name}Templates, mbuiContext.statementContext()));
                                        </#if>
                                    </#list>
                dialog.show();
            })
                                <#else>
            .button(mbuiContext.tableButtonFactory().add(Ids.build("${table.selector}", Ids.ADD_SUFFIX), ${table.title},
                ${table.metadata.name}Template,
                () -> presenter.reload(),
                                    <#list action.attributes as attribute>
                "${attribute.name}"<#if attribute_has_next>, <#else>))</#if>
                                    </#list>
                                </#if>
                            <#else>
            .button(mbuiContext.tableButtonFactory().add(Ids.build("${table.selector}", Ids.ADD_SUFFIX), ${table.title},
                ${table.metadata.name}Template,
                () -> presenter.reload()))
                            </#if>
                            <#break>
                        <#case "REMOVE_RESOURCE">
            .button(mbuiContext.tableButtonFactory().remove(${table.title}, ${table.metadata.name}Template,
                (api) -> ${action.nameResolver},
                () -> presenter.reload()))
                            <#break>
                    </#switch>
                <#else>
            .button(${action.title}, <#if action.scope??>Button.Scope.${action.scope}, </#if>(event, api) -> ${action.handler})
                </#if>
            </#list>
            <#if table.onlySimpleColumns>
            .columns(<#list table.columns as column>"${column.name}"<#if column_has_next>, </#if></#list>)
            <#else>
                <#list table.columns as column>
                    <#if column.value??>
            .column("${column.name}", (cell, type, row, meta) -> ${column.value})
                    <#else>
            .column("${column.name}")
                    </#if>
                </#list>
            </#if>
            .build();
        ${table.name} = new ModelNodeTable<>("${table.selector}", ${table.name}Options);
        </#list>

        <#list context.attachables as attachable>
        registerAttachable(${attachable.name});
        </#list>

        <#list context.tabs as tabInfo>
            <#list tabInfo.items as tabItem>
                <#list tabItem.formChildren as tabChild>
        ${tabInfo.name}.add(Ids.build("${tabInfo.name}", "tab", "${tabItem.id}"), "${tabItem.title}", ${context.findFormById(tabChild)}.asElement());
                </#list>
            </#list>
        </#list>

        <#if context.verticalNavigation??>
        ${context.verticalNavigation.name} = new VerticalNavigation();
            <#list context.verticalNavigation.items as primaryItem>
                <#if primaryItem.content?has_content>
        Elements.Builder ${primaryItem.name}Builder = new Elements.Builder()
            .div()
                    <#list primaryItem.content as content>
                        <#if content.html??>
                .div()
                    .innerHtml(SafeHtmlUtils.fromSafeConstant("${content.html}"))
                    .rememberAs("${content.name}")
                .end()
                        <#elseif content.tab>
                .add(${context.findTabNameByTabId(content.reference)})
                        <#elseif content.reference??>
                .add(${content.reference})
                        </#if>
                    </#list>
            .end();
        Element ${primaryItem.name}Element = ${primaryItem.name}Builder.build();
                    <#list primaryItem.content as content>
                        <#if content.html??>
        handlebarElements.put("${content.name}", ${primaryItem.name}Builder.referenceFor("${content.name}"));
                        </#if>
                    </#list>
        ${context.verticalNavigation.name}.addPrimary("${primaryItem.id}", ${primaryItem.title}<#if primaryItem.icon??>, "${primaryItem.icon}"</#if>, ${primaryItem.name}Element);
                <#elseif primaryItem.subItems?has_content>
        ${context.verticalNavigation.name}.addPrimary("${primaryItem.id}", ${primaryItem.title}<#if primaryItem.icon??>, "${primaryItem.icon}"</#if>);
                    <#list primaryItem.subItems as subItem>
                        <#if subItem.content?has_content>
        Elements.Builder ${subItem.name}Builder = new Elements.Builder()
            .div()
                            <#list subItem.content as content>
                                <#if content.html??>
                .div()
                    .innerHtml(SafeHtmlUtils.fromSafeConstant("${content.html}"))
                    .rememberAs("${content.name}")
                .end()
                                <#elseif content.tab>
                .add(${context.findTabNameByTabId(content.reference)})
                                <#elseif content.reference??>
                .add(${content.reference})
                                </#if>
                            </#list>
            .end();
        Element ${subItem.name}Element = ${subItem.name}Builder.build();
                            <#list subItem.content as content>
                                <#if content.html??>
        handlebarElements.put("${content.name}", ${subItem.name}Builder.referenceFor("${content.name}"));
                                </#if>
                            </#list>
        ${context.verticalNavigation.name}.addSecondary("${primaryItem.id}", "${subItem.id}", ${subItem.title}, ${subItem.name}Element);
                        </#if>
                    </#list>
                </#if>
            </#list>
        LayoutBuilder layoutBuilder = new LayoutBuilder()
            .row()
                .column()
                    .addAll(${context.verticalNavigation.name}.panes())
                .end()
            .end();
        <#else>
            <#if context.content?has_content>
        LayoutBuilder layoutBuilder = new LayoutBuilder()
            .row()
                .column()
                <#list context.content as content>
                    <#if content.html??>
                    .div()
                        .innerHtml(SafeHtmlUtils.fromSafeConstant("${content.html}"))
                        .rememberAs("${content.name}")
                    .end()
                    <#elseif content.tab>
                    .add(${context.findTabNameByTabId(content.reference)})
                    <#elseif content.reference??>
                    .add(${content.reference})
                    </#if>
                </#list>
                .end()
            .end();
                <#list context.content as content>
                    <#if content.html??>
            handlebarElements.put("${content.name}", layoutBuilder.referenceFor("${content.name}"));
                    </#if>
                </#list>
            <#else>
        LayoutBuilder layoutBuilder = new LayoutBuilder()
            .row()
                .column()
                    .h(1).textContent("${context.base}").end()
                    .p().textContent(org.jboss.hal.resources.Names.NYI).end()
                .end()
            .end();
            </#if>
        </#if>

        Element root = layoutBuilder.build();
        initElement(root);

        <#-- @PostConstruct -->
        <#list context.postConstructs as postConstruct>
        ${postConstruct.name}();
        </#list>
    }
    <#-- Abstract properties -->
    <#list context.abstractProperties as abstractProperty>

    @Override
    ${abstractProperty.modifier}${abstractProperty.type} ${abstractProperty.method}() {
    return ${abstractProperty.field};
    }
    </#list>

    @Override
    public void attach() {
        super.attach();

        <#list context.dataTables as table>
            <#if table.formRef??>
        ${table.name}.api().bindForm(${table.formRef.name});
            </#if>
        </#list>

        <#if context.verticalNavigation??>
            <#list context.verticalNavigation.items as primaryItem>
                <#list primaryItem.content as content>
                    <#if content.html??>
                        <#list content.handlebars?keys as handlebar>
        TemplateUtil.replaceHandlebar(handlebarElements.get("${content.name}"), "${handlebar}", String.valueOf(${content.handlebars?values[handlebar_index]}));
                        </#list>
                    </#if>
                </#list>
                <#list primaryItem.subItems as subItem>
                    <#list subItem.content as content>
                        <#if content.html??>
                            <#list content.handlebars?keys as handlebar>
        TemplateUtil.replaceHandlebar(handlebarElements.get("${content.name}"), "${handlebar}", String.valueOf(${content.handlebars?values[handlebar_index]}));
                            </#list>
                        </#if>
                    </#list>
                </#list>
            </#list>
        <#else>
            <#list context.content as content>
                <#if content.html??>
                    <#list content.handlebars?keys as handlebar>
        TemplateUtil.replaceHandlebar(handlebarElements.get("${content.name}"), "${handlebar}", String.valueOf(${content.handlebars?values[handlebar_index]}));
                    </#list>
                </#if>
            </#list>
        </#if>
    }
}
