package br.edu.ufersa.cc.pd;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.gateway.Gateway;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class.getSimpleName());
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(final String[] args) {
        LOG.info("Iniciando Gateway...");

        // RabbitMQ Consumer for receiving drone messages
        final var mqConsumerFromDrones = new GatewayConnection("climate_data.send", "drones", "fanout", "",
                "UTF-8");
        mqConsumerFromDrones.createConnection();

        // MQTT Connection
        final var mqttConnection = new GatewayConnectionMqtt("climate_data");

        final var mqProducerFromDrones = new GatewayConnection("climate_data.all", "client", "fanout", "",
                "UTF-8");
        mqProducerFromDrones.createConnection();

        // final var port = Integer.parseInt(System.getenv("GATEWAY_PORT"));
        final var port = 8990;
        final var gateway = new Gateway(port, mqConsumerFromDrones, mqttConnection, mqProducerFromDrones);
        EXECUTOR.submit(gateway);
    }

}
