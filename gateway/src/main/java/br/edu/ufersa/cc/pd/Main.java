package br.edu.ufersa.cc.pd;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.gateway.Gateway;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class.getSimpleName());
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(final String[] args) throws InterruptedException {
        LOG.info("Iniciando Gateway...");
        final var mqConsumerFromDrones = new GatewayConnection("drones", "fanout", "", "UTF-8");
        mqConsumerFromDrones.createConnection();

        final var port = Integer.parseInt(System.getenv("GATEWAY_PORT"));
        final var gateway = new Gateway(port, mqConsumerFromDrones);
        EXECUTOR.submit(gateway);

        // EXECUTOR.shutdown();
        // EXECUTOR.awaitTermination(1, TimeUnit.MINUTES);
    }

}
