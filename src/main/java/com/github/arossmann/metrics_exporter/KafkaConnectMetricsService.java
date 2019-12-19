package com.github.arossmann.metrics_exporter;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@RegisterRestClient
public interface KafkaConnectMetricsService {

    // define calling method
    @GET
    @Path("/connectors/{name}/status")
    @Produces("application/json")
    KafkaConnectMetrics getStatusByConnectorName(@PathParam String name);
}
