package br.edu.ufersa.cc.pd;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import br.edu.ufersa.cc.pd.gateway.Gateway;

public class Main {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(final String[] args) throws InterruptedException {
        final var mqConsumerFromDrones = new GatewayConnection(null, null, null, null);

        final var port = Integer.parseInt(System.getenv("GATEWAY_PORT"));
        final var gateway = new Gateway(port, mqConsumerFromDrones);
        EXECUTOR.submit(gateway);

        EXECUTOR.shutdown();
        EXECUTOR.awaitTermination(1, TimeUnit.MINUTES);
    }

}
