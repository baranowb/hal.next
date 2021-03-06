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
package org.jboss.hal.ballroom.typeahead;

import java.util.List;

import org.jboss.hal.dmr.ExternalModelNode;
import org.jboss.hal.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Harald Pehl
 */
@SuppressWarnings({"HardCodedStringLiteral", "DuplicateStringLiteralInspection"})
public class NamesResultProcessorTest {

    private static final String[] NAMES = new String[]{
            "management",
            "private",
            "public",
            "unsecure"
    };

    private NamesResultProcessor resultProcessor;
    private ModelNode result;

    @Before
    public void setUp() throws Exception {
        resultProcessor = new NamesResultProcessor();
        result = ExternalModelNode.read(NamesResultProcessorTest.class.getResourceAsStream("names.dmr"));
    }

    @Test
    public void nullQuery() throws Exception {
        List<String> models = resultProcessor.processToModel(null, result);
        assertTrue(models.isEmpty());
    }

    @Test
    public void emptyQuery() throws Exception {
        List<String> models = resultProcessor.processToModel("", result);
        assertTrue(models.isEmpty());
    }

    @Test
    public void wildcardQuery() throws Exception {
        List<String> models = resultProcessor.processToModel("*", result);
        assertArrayEquals(NAMES, models.toArray());
    }

    @Test
    public void oneMatch() throws Exception {
        List<String> models = resultProcessor.processToModel("g", result);
        assertArrayEquals(new String[]{"management"}, models.toArray());
    }

    @Test
    public void twoMatches() throws Exception {
        List<String> models = resultProcessor.processToModel("p", result);
        assertArrayEquals(new String[]{"private", "public"}, models.toArray());
    }

    @Test
    public void noMatches() throws Exception {
        List<String> models = resultProcessor.processToModel("foo", result);
        assertTrue(models.isEmpty());
    }
}