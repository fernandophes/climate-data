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

        // Fila que recebe os dados dos drones
        final var mqConsumerFromDrones = new GatewayConnection("drones.send", "drones", "send", "UTF-8");
        mqConsumerFromDrones.createConnection();

        // Fila para publisher em tempo real
        final var realTimeMqProducer = new GatewayConnection("publisher.real_time", "publisher", "real_time", "UTF-8");
        realTimeMqProducer.createConnection();

        // Fila para publisher sob demanda
        final var onDemandMqProducer = new GatewayConnection("publisher.on_demand", "publisher", "on_demand", "UTF-8");
        onDemandMqProducer.createConnection();

        final var port = Integer.parseInt(System.getenv("GATEWAY_PORT"));
        final var gateway = new Gateway(port, mqConsumerFromDrones, realTimeMqProducer, onDemandMqProducer);
        EXECUTOR.submit(gateway);
    }

}
