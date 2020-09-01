
/*
 * Camel EndpointConfiguration generated by camel-api-component-maven-plugin
 */
package org.apache.camel.component.braintree;

import org.apache.camel.spi.Configurer;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;

/**
 * Camel EndpointConfiguration for com.braintreegateway.CustomerGateway
 */
@UriParams(apiName = "customer")
@Configurer
public final class CustomerGatewayEndpointConfiguration extends BraintreeConfiguration {
    @UriParam(description = "The id of the association filter to use")
    private String associationFilterId;
    @UriParam(description = "The id of the")
    private String id;
    @UriParam(description = "The request query to use for search")
    private com.braintreegateway.CustomerSearchRequest query;
    @UriParam(description = "The request")
    private com.braintreegateway.CustomerRequest request;

    public String getAssociationFilterId() {
        return associationFilterId;
    }

    public void setAssociationFilterId(String associationFilterId) {
        this.associationFilterId = associationFilterId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public com.braintreegateway.CustomerSearchRequest getQuery() {
        return query;
    }

    public void setQuery(com.braintreegateway.CustomerSearchRequest query) {
        this.query = query;
    }

    public com.braintreegateway.CustomerRequest getRequest() {
        return request;
    }

    public void setRequest(com.braintreegateway.CustomerRequest request) {
        this.request = request;
    }
}
