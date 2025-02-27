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
package org.apache.camel.component.jms.issues;

import javax.jms.ConnectionFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.CamelJmsTestHelper;
import org.apache.camel.component.jms.JmsMessage;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.apache.camel.component.jms.JmsComponent.jmsComponentAutoAcknowledge;
import static org.apache.camel.test.junit5.TestSupport.assertIsInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JmsMutateMessageTest extends CamelTestSupport {

    private final String uri = "activemq:queue:hello";

    @Test
    public void testMutateMessage() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).body().isEqualTo("Hello World");
        mock.message(0).header("HEADER_1").isEqualTo("VALUE_2");

        template.sendBodyAndHeader(uri, "Hello World", "HEADER_1", "VALUE_1");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
        ConnectionFactory connectionFactory = CamelJmsTestHelper.createConnectionFactory();
        camelContext.addComponent("activemq", jmsComponentAutoAcknowledge(connectionFactory));
        return camelContext;
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(uri)
                        .process(exchange -> {
                            // do not mutate it
                            JmsMessage msg = assertIsInstanceOf(JmsMessage.class, exchange.getIn());
                            assertNotNull(msg.getJmsMessage(), "javax.jms.Message should not be null");

                            // get header should not mutate it
                            assertEquals("VALUE_1", exchange.getIn().getHeader("HEADER_1"));
                        })
                        // setting a new header should mutate it
                        .setHeader("HEADER_1", constant("VALUE_2"))
                        .process(exchange -> {
                            // it should have been mutated
                            JmsMessage msg = assertIsInstanceOf(JmsMessage.class, exchange.getIn());
                            assertNotNull(msg.getJmsMessage(), "javax.jms.Message should not be null");

                            // get header should not mutate it
                            assertEquals("VALUE_2", exchange.getIn().getHeader("HEADER_1"));
                        })
                        .to("mock:result");
            }
        };
    }

}
