# Metrics Exporter


This Service is based on the [Quarkus](https://quarkus.io/) Framework.

## Config

The file resources/application.properties contains the main config:

```
# KafkaConnect Metrics
kafkaconnect.plugin.name=mqtt-source
kafkaconnect.refresh.rate=15

# rest interface for kafka connect
KafkaConnectMetricsService/mp-rest/url=http://localhost:8083

# Configure the Kafka sink (we write to it)
mp.messaging.outgoing.kafkaconnect-metrics.bootstrap.servers=localhost:29092
mp.messaging.outgoing.kafkaconnect-metrics.connector=smallrye-kafka
mp.messaging.outgoing.kafkaconnect-metrics.topic=kafkaconnect-metrics
mp.messaging.outgoing.kafkaconnect-metrics.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

| field                                                         | description | example                                                | 
|---------------------------------------------------------------|-------------|--------------------------------------------------------|
| kafkaconnect.plugin.name                                      |  Name of the plugin           | mqtt-source                                            | 
| kafkaconnect.refresh.rate                                     |  Refreshrate in Seconds           | 15                                                     | 
| KafkaConnectMetricsService/mp-rest/url |  URL for the KafkaConnect REST API           | http://localhost:8083                                  | 
| mp.messaging.outgoing.kafkaconnect-metrics.bootstrap.servers  |  URL for the Kafka Bootstrap Server            | localhost:29092                                        | 
| mp.messaging.outgoing.kafkaconnect-metrics.connector          |  The Kafka Connector           | smallrye-kafka                                         | 
| mp.messaging.outgoing.kafkaconnect-metrics.topic              |  Topic Name to push the kafkaConnect metrics to           | kafkaconnect-metrics                                   | 
| mp.messaging.outgoing.kafkaconnect-metrics.value.serializer   |  Serializer Type           | org.apache.kafka.common.serialization.StringSerializer | 

## KafkaConnect Metrics

The implementation of the KafkaConnect metrics is based on the official [Rest API](https://docs.confluent.io/current/connect/references/restapi.html#get--connectors-(string-name)-status)

### Response JSON Object
 	
* name (string) – The name of the connector.
* connector (map) – The map containing connector status.
* tasks[i] (map) – The map containing the task status.

### KafkaConnectMetrics Class

The corresponding Java Object is defined in the [KafkaConnectMetrics.java](src/main/java/com/github/arossmann/metrics_exporter/metrics_exporter/KafkaConnectMetrics.java) file:
```
package com.github.arossmann.metrics_exporter;

import java.util.List;

public class KafkaConnectMetrics {
    public String name;
    public String type;
    public Connector connector;
    public List<Task> tasks;

    public static class Connector{
        public String state;
        public String trace;
        public String worker_id;
    }

    public static class Task{
        public String state;
        public Integer id;
        public String worker_id;
    }
}
```

### KafkaConnectMetricsService
In the Interface [KafkaConnectMetricsService](src/main/java/com/github/arossmann/metrics_exporter/metrics_exporter/KafkaConnectMetricsService.java) the method _getStatusByConnectorName_ is defined as calling the Rest API by the provided connector name.

### KafkaConnectMetricsResource

[Here](src/main/java/com/github/arossmann/metrics_exporter/metrics_exporter/KafkaConnectMetricsResource.java) the calling of the API is declared as well as the pushing of the information into the defined Kafka topic.

```
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
```

# How to run

## development (=local) run

```
./mvnw compile quarkus:dev
```

## creating container
1) create jar file from code

```
mvn package
```
2) build container

```
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/metrics-exporter-jvm .
```

3) Run the container

```
docker run -i --rm -p 8080:8080 quarkus/metrics-exporter-jvm
```
