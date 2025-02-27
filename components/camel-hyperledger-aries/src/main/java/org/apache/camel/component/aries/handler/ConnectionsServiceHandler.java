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
package org.apache.camel.component.aries.handler;

import org.apache.camel.Exchange;
import org.apache.camel.component.aries.HyperledgerAriesEndpoint;
import org.apache.camel.component.aries.UnsupportedServiceException;
import org.hyperledger.acy_py.generated.model.ConnectionInvitation;
import org.hyperledger.aries.api.connection.ConnectionAcceptInvitationFilter;
import org.hyperledger.aries.api.connection.ConnectionReceiveInvitationFilter;
import org.hyperledger.aries.api.connection.ConnectionRecord;
import org.hyperledger.aries.api.connection.CreateInvitationRequest;
import org.hyperledger.aries.api.connection.CreateInvitationResponse;
import org.hyperledger.aries.api.connection.ReceiveInvitationRequest;

public class ConnectionsServiceHandler extends AbstractServiceHandler {

    public ConnectionsServiceHandler(HyperledgerAriesEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange, String service) throws Exception {

        if (service.equals("/connections/create-invitation")) {
            CreateInvitationRequest reqObj = maybeBody(exchange, CreateInvitationRequest.class);
            if (reqObj == null) {
                reqObj = CreateInvitationRequest.builder().build();
            }
            CreateInvitationResponse resObj = createClient().connectionsCreateInvitation(reqObj).get();
            exchange.getIn().setBody(resObj);

        } else if (service.equals("/connections/receive-invitation")) {
            ReceiveInvitationRequest reqObj = maybeBody(exchange, ReceiveInvitationRequest.class);
            if (reqObj == null) {
                ConnectionInvitation invitation = assertBody(exchange, ConnectionInvitation.class);
                reqObj = ReceiveInvitationRequest.builder()
                        .recipientKeys(invitation.getRecipientKeys())
                        .serviceEndpoint(invitation.getServiceEndpoint())
                        .build();
            }
            ConnectionReceiveInvitationFilter filter = maybeHeader(exchange, ConnectionReceiveInvitationFilter.class);
            ConnectionRecord resObj = createClient().connectionsReceiveInvitation(reqObj, filter).get();
            exchange.getIn().setBody(resObj);

        } else if (service.equals("/connections/accept-invitation")) {
            String connectionId = assertBody(exchange, String.class);
            ConnectionAcceptInvitationFilter acceptFilter = maybeHeader(exchange, ConnectionAcceptInvitationFilter.class);
            ConnectionRecord resObj = createClient().connectionsAcceptInvitation(connectionId, acceptFilter).get();
            exchange.getIn().setBody(resObj);

        } else if (service.equals("/connections/get-single")) {
            String connectionId = assertBody(exchange, String.class);
            ConnectionRecord resObj = createClient().connectionsGetById(connectionId).get();
            exchange.getIn().setBody(resObj);

        } else {
            throw new UnsupportedServiceException(service);
        }
    }
}
