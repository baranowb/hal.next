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
package org.jboss.hal.client.configuration.subsystem.batch;

import java.util.List;

import org.jboss.hal.ballroom.VerticalNavigation;
import org.jboss.hal.ballroom.form.Form;
import org.jboss.hal.ballroom.table.Api.RefreshMode;
import org.jboss.hal.ballroom.table.DataTable;
import org.jboss.hal.core.mbui.MbuiContext;
import org.jboss.hal.core.mbui.MbuiViewImpl;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.model.NamedNode;
import org.jboss.hal.spi.MbuiElement;
import org.jboss.hal.spi.MbuiView;

/**
 * @author Claudio Miranda
 */
@MbuiView
@SuppressWarnings({"DuplicateStringLiteralInspection", "HardCodedStringLiteral", "WeakerAccess"})
public abstract class BatchView extends MbuiViewImpl<BatchPresenter> implements BatchPresenter.MyView {

    // ------------------------------------------------------ initialization

    public static BatchView create(final MbuiContext mbuiContext) {
        return new Mbui_BatchView(mbuiContext);
    }

    @MbuiElement("batch-vertical-navigation") VerticalNavigation navigation;
    @MbuiElement("configuration-form") Form<ModelNode> configurationForm;
    @MbuiElement("in-memory-job-repo-table") DataTable<NamedNode> inMemoryJobRepoTable;
    @MbuiElement("in-memory-job-repo-form") Form<NamedNode> inMemoryJobRepoForm;
    @MbuiElement("jdbc-job-repo-table") DataTable<NamedNode> jdbcJobRepoTable;
    @MbuiElement("jdbc-job-repo-form") Form<NamedNode> jdbcJobRepoForm;
    @MbuiElement("thread-factory-table") DataTable<NamedNode> threadFactoryTable;
    @MbuiElement("thread-factory-form") Form<NamedNode> threadFactoryForm;
    @MbuiElement("thread-pool-table") DataTable<NamedNode> threadPoolTable;
    @MbuiElement("thread-pool-form") Form<NamedNode> threadPoolForm;

    BatchView(final MbuiContext mbuiContext) {
        super(mbuiContext);
    }


    // ------------------------------------------------------ form and table updates from DMR

    @Override
    public void updateConfiguration(ModelNode configuration) {
        configurationForm.view(configuration);
    }

    @Override
    public void updateInMemoryJobRepository(final List<NamedNode> items) {
        inMemoryJobRepoTable.api().clear().add(items).refresh(RefreshMode.RESET);
        inMemoryJobRepoForm.clear();
    }

    @Override
    public void updateJdbcJobRepository(final List<NamedNode> items) {
        jdbcJobRepoTable.api().clear().add(items).refresh(RefreshMode.RESET);
        jdbcJobRepoForm.clear();
    }

    @Override
    public void updateThreadFactory(final List<NamedNode> items) {
        threadFactoryTable.api().clear().add(items).refresh(RefreshMode.RESET);
        threadFactoryForm.clear();
    }

    @Override
    public void updateThreadPool(final List<NamedNode> items) {
        threadPoolTable.api().clear().add(items).refresh(RefreshMode.RESET);
        threadPoolForm.clear();
    }

    // ------------------------------------------------------ view / mbui contract

    @Override
    public VerticalNavigation getVerticalNavigation() {
        return navigation;
    }
}
