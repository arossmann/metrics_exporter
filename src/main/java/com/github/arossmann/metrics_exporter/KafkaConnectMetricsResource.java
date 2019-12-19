package com.github.arossmann.metrics_exporter;

import com.google.gson.GsonBuilder;
import io.reactivex.Flowable;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import com.google.gson.Gson;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class KafkaConnectMetricsResource {

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    @Inject
    // invoke the Rest Client to call the KafkaConnect Service
    @RestClient
    KafkaConnectMetricsService kafkaConnectMetricsService;

    // get the properties
    @ConfigProperty(name = "kafkaconnect.plugin.name")
    String pluginName;
    @ConfigProperty(name = "kafkaconnect.refresh.rate")
    Integer refreshRate;

    // by the given refresh rate, call the Rest API and push the result to the Kafka topic
    @Outgoing("kafkaconnect-metrics")
    public Flowable<String> generate() {
        return Flowable.interval(refreshRate, TimeUnit.SECONDS)
                .map(tick -> gson.toJson(kafkaConnectMetricsService.getStatusByConnectorName(pluginName)));
    }

}
