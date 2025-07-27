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

        final var mqProducerToClientQueue = new GatewayConnection("client_http.on_demand.all", "client_http", "fanout", "",
                "UTF-8");
        mqProducerToClientQueue.createConnection();

        // final var port = Integer.parseInt(System.getenv("GATEWAY_PORT"));
        final var port = 8091;
        final var gateway = new Gateway(port, mqConsumerFromDrones, mqProducerToClientQueue);
        EXECUTOR.submit(gateway);
    }

}
