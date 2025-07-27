package br.edu.ufersa.cc.pd;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.drone.Drone;
import br.edu.ufersa.cc.pd.mq.DroneConnection;
import br.edu.ufersa.cc.pd.utils.Constants;
import br.edu.ufersa.cc.pd.utils.dto.DataFormat;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class.getSimpleName());
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(final String[] args) throws InterruptedException {
        LOG.info("Iniciando drone...");

        try {
            final var drone = launch();

            // Fix: Add queue parameter as first argument
            final var mqConnection = new DroneConnection("drones.send", "drones",  "send", "UTF-8");
            mqConnection.createConnection();

            drone.subscribe(mqConnection::send);

            // Submit drone to executor
            EXECUTOR.submit(drone);
        } catch (Exception e) {
            LOG.error("Failed to start drone", e);
            System.exit(1);
        }
    }

    private static Drone launch() {
        LOG.info("Attempting to launch drone via environment variables...");
        try {
            return launchViaEnv();
        } catch (final Exception e) {
            LOG.error("Failed to launch via environment variables: {}", e.getMessage(), e);
            LOG.info("Falling back to console input...");
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
        LOG.info("Reading environment variables for drone configuration...");

        final var port = System.getenv("DRONE_PORT");
        final var name = System.getenv("DRONE_NAME");
        final var delimiter = System.getenv("DRONE_DELIMITER");
        final var start = System.getenv("DRONE_START");
        final var end = System.getenv("DRONE_END");

        // Log environment variables for debugging
        LOG.info(
                "Environment variables - DRONE_PORT: {}, DRONE_NAME: {}, DRONE_DELIMITER: {}, DRONE_START: {}, DRONE_END: {}",
                port, name, delimiter, start, end);

        // Validate environment variables
        if (port == null || name == null || delimiter == null || start == null || end == null) {
            throw new IllegalStateException("Missing required environment variables for drone configuration");
        }

        final var portInt = Integer.parseInt(port);
        final var address = new InetSocketAddress(Constants.getDefaultHost(), portInt);
        final var format = new DataFormat(delimiter, start, end);

        LOG.info("Creating drone - Name: {}, Port: {}, Format: delimiter='{}', start='{}', end='{}'",
                name, portInt, delimiter, start, end);

        return new Drone(address, 0, name, format);
    }

}
