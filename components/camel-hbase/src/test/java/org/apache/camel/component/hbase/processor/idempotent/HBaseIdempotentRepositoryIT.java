/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.hbase.processor.idempotent;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hbase.CamelHBaseTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.hadoop.hbase.TableExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HBaseIdempotentRepositoryIT extends CamelHBaseTestSupport {

    IdempotentRepository repository;

    private String key01 = "123";
    private String key02 = "456";

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        this.repository = new HBaseIdempotentRepository(service.getConfiguration(), PERSON_TABLE, INFO_FAMILY, "mycolumn");
        super.setUp();
        try {
            createTable(PERSON_TABLE, INFO_FAMILY);
        } catch (TableExistsException ex) {
            //Ignore if table exists
        }
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        deleteTable(PERSON_TABLE);
        super.tearDown();
    }

    @Test
    public void testAdd() {
        // add first key
        assertTrue(repository.add(key01));
        assertTrue(repository.contains(key01));

        // try to add an other one
        assertTrue(repository.add(key02));
        assertTrue(repository.contains(key02));

        // try to add the first key again
        assertFalse(repository.add(key01));
    }

    @Test
    public void testContains() {
        assertFalse(repository.contains(key01));

        // add key and check again
        assertTrue(repository.add(key01));
        assertTrue(repository.contains(key01));
    }

    @Test
    public void testRemove() {
        // add key to remove
        assertTrue(repository.add(key01));
        assertTrue(repository.contains(key01));
        // assertEquals(1, dataSet.size());

        // remove key
        assertTrue(repository.remove(key01));
        //assertEquals(0, dataSet.size());

        // try to remove a key that isn't there
        assertFalse(repository.remove(key02));
    }

    @Test
    public void testClear() {
        // add key to remove
        assertTrue(repository.add(key01));
        assertTrue(repository.add(key02));
        assertTrue(repository.contains(key01));
        assertTrue(repository.contains(key02));

        // remove key
        repository.clear();

        assertFalse(repository.contains(key01));
        assertFalse(repository.contains(key02));
    }

    @Test
    public void testConfirm() {
        // it always return true
        assertTrue(repository.confirm(key01));
    }

    @Test
    public void testRepositoryInRoute() throws Exception {
        MockEndpoint mock = (MockEndpoint) context.getEndpoint("mock:out");
        mock.expectedBodiesReceived("a", "b");
        // c is a duplicate

        // send 3 message with one duplicated key (key01)
        template.sendBodyAndHeader("direct:in", "a", "messageId", key01);
        template.sendBodyAndHeader("direct:in", "b", "messageId", key02);
        template.sendBodyAndHeader("direct:in", "c", "messageId", key01);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:in")
                        .idempotentConsumer(header("messageId"), repository)
                        .to("mock:out");
            }
        };
    }
}
