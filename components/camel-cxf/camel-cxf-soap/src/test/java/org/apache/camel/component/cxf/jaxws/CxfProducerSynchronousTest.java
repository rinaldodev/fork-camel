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
package org.apache.camel.component.cxf.jaxws;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.CXFTestSupport;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.cxf.BusFactory;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CxfProducerSynchronousTest extends CamelTestSupport {

    private static final String SIMPLE_SERVER_ADDRESS
            = "http://localhost:" + CXFTestSupport.getPort1() + "/CxfProducerSynchronousTest/test";
    private static final String REQUEST_MESSAGE = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                                                  + "<soap:Body><ns1:echo xmlns:ns1=\"http://jaxws.cxf.component.camel.apache.org/\">"
                                                  + "<arg0 xmlns=\"http://jaxws.cxf.component.camel.apache.org/\">Hello World!</arg0>"
                                                  + "</ns1:echo></soap:Body></soap:Envelope>";

    private static final String TEST_MESSAGE = "Hello World!";
    private static String beforeThreadName;
    private static String afterThreadName;

    private String url = "cxf://" + SIMPLE_SERVER_ADDRESS
                         + "?serviceClass=org.apache.camel.component.cxf.jaxws.HelloService&dataFormat=RAW&synchronous=true";

    @BeforeAll
    public static void startServer() throws Exception {
        // start a simple front service
        ServerFactoryBean svrBean = new ServerFactoryBean();
        svrBean.setAddress(SIMPLE_SERVER_ADDRESS);
        svrBean.setServiceClass(HelloService.class);
        svrBean.setServiceBean(new HelloServiceImpl());
        svrBean.setBus(BusFactory.getDefaultBus());
        svrBean.create();
    }

    @Test
    public void testSynchronous() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);

        String response = template.requestBody("direct:start", REQUEST_MESSAGE, String.class);
        assertTrue(response.indexOf("echo " + TEST_MESSAGE) > 0, "It should has the echo message");
        assertTrue(response.indexOf("echoResponse") > 0, "It should has the echoResponse tag");

        assertMockEndpointsSatisfied();

        assertTrue(beforeThreadName.equalsIgnoreCase(afterThreadName), "Should use same threads");
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .to("log:before")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                beforeThreadName = Thread.currentThread().getName();
                            }
                        })
                        .to(url)
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                afterThreadName = Thread.currentThread().getName();
                            }
                        })
                        .to("log:after")
                        .to("mock:result");
            }
        };
    }

}
