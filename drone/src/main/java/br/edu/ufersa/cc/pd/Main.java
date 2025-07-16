package br.edu.ufersa.cc.pd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.drone.Drone;
import br.edu.ufersa.cc.pd.mq.DroneConnection;
import br.edu.ufersa.cc.pd.utils.Constants;
import br.edu.ufersa.cc.pd.utils.dto.DataFormat;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class.getSimpleName());
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Timer TIMER = new Timer();

    public static void main(final String[] args) throws InterruptedException {
        LOG.info("Iniciando drone...");
        final var drone = launch();

        // Fix: Add queue parameter as first argument
        final var mqConnection = new DroneConnection("drones.climate_data.send", "drones", "fanout", "", "UTF-8");
        mqConnection.createConnection();

        drone.subscribe(message -> {
            LOG.info("Enviando mensagem... {}", message);
            mqConnection.send(message);
        });

        // Submit drone to executor
        EXECUTOR.submit(drone);

        // Set up timer for automatic shutdown after 3 minutes
        final var cancellation = new TimerTask() {
            @Override
            public void run() {
                try {
                    LOG.info("Finalizando drone automaticamente...");
                    drone.close();
                    mqConnection.close();
                    EXECUTOR.shutdown();
                } catch (final IOException e) {
                    LOG.error("Erro ao finalizar drone automaticamente", e);
                }
            }
        };

        TIMER.schedule(cancellation, 3 * 60_000L);

        // Wait for shutdown (either by timer or external signal)
        EXECUTOR.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    private static Drone launch() {
        try {
            return launchViaEnv();
        } catch (final Exception e) {
            return launchViaConsole();
        }
    }

    private static Drone launchViaConsole() {
        System.out.println("### NOVO DRONE ###");
        final var input = new Scanner(System.in);

        System.out.print("Nome: ");
        final var name = input.nextLine().trim();

        System.out.print("Separador: ");
        final var delimiter = input.nextLine();

        System.out.print("Abertura: ");
        final var start = input.nextLine();

        System.out.print("Fechamento: ");
        final var end = input.nextLine();

        System.out.print("Porta: ");
        final var port = input.nextInt();

        input.close();

        final var address = new InetSocketAddress(Constants.getDefaultHost(), port);
        final var format = new DataFormat(delimiter, start, end);

        return new Drone(address, 0, name, format);
    }

    private static Drone launchViaEnv() {
        final var port = Integer.parseInt(System.getenv("DRONE_PORT"));
        final var name = System.getenv("DRONE_NAME");
        final var delimiter = System.getenv("DRONE_DELIMITER");
        final var start = System.getenv("DRONE_START");
        final var end = System.getenv("DRONE_END");

        final var address = new InetSocketAddress(Constants.getDefaultHost(), port);
        final var format = new DataFormat(delimiter, start, end);

        return new Drone(address, 0, name, format);
    }

}
