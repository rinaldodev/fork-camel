
/*
 * Camel EndpointConfiguration generated by camel-api-component-maven-plugin
 */
package org.apache.camel.component.fhir;

import org.apache.camel.spi.Configurer;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;

/**
 * Camel EndpointConfiguration for org.apache.camel.component.fhir.api.FhirCreate
 */
@UriParams(apiName = "create")
@Configurer
public final class FhirCreateEndpointConfiguration extends FhirConfiguration {
    @UriParam(description = "See")
    private java.util.Map<org.apache.camel.component.fhir.api.ExtraParameters,Object> extraParameters;
    @UriParam(description = "Add a")
    private ca.uhn.fhir.rest.api.PreferReturnEnum preferReturn;
    @UriParam(description = "The resource to create")
    private org.hl7.fhir.instance.model.api.IBaseResource resource;
    @UriParam(description = "The resource to create")
    private String resourceAsString;
    @UriParam(description = "The search URL to use. The format of this URL should be of the form")
    private String url;

    public java.util.Map<org.apache.camel.component.fhir.api.ExtraParameters,Object> getExtraParameters() {
        return extraParameters;
    }

    public void setExtraParameters(java.util.Map<org.apache.camel.component.fhir.api.ExtraParameters,Object> extraParameters) {
        this.extraParameters = extraParameters;
    }

    public ca.uhn.fhir.rest.api.PreferReturnEnum getPreferReturn() {
        return preferReturn;
    }

    public void setPreferReturn(ca.uhn.fhir.rest.api.PreferReturnEnum preferReturn) {
        this.preferReturn = preferReturn;
    }

    public org.hl7.fhir.instance.model.api.IBaseResource getResource() {
        return resource;
    }

    public void setResource(org.hl7.fhir.instance.model.api.IBaseResource resource) {
        this.resource = resource;
    }

    public String getResourceAsString() {
        return resourceAsString;
    }

    public void setResourceAsString(String resourceAsString) {
        this.resourceAsString = resourceAsString;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
