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
