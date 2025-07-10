package br.edu.ufersa.cc.pd.api.launchers;

import java.net.InetSocketAddress;
import java.util.Scanner;

import br.edu.ufersa.cc.pd.api.apps.Drone;
import br.edu.ufersa.cc.pd.api.contracts.Launcher;
import br.edu.ufersa.cc.pd.api.utils.Constants;

public class DroneLauncher extends Launcher<Drone> {

    public DroneLauncher() {
        super("Drone");
    }

    @Override
    public Drone launch() {
        try {
            return launchViaEnv();
        } catch (Exception e) {
            return launchViaConsole();
        }
    }

    private Drone launchViaConsole() {
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


        System.out.println("Porta: ");
        final var port = input.nextInt();
        
        input.close();
        
        final var address = new InetSocketAddress(Constants.getDefaultHost(), port);
        final var format = new Drone.DataFormat(delimiter, start, end);
        final var drone = new Drone(address, 0, name, format);
        drone.run();

        return drone;
    }

    private Drone launchViaEnv() {
        final var port = Integer.parseInt(System.getenv("DRONE_PORT"));
        final var name = System.getenv("DRONE_NAME");
        final var delimiter = System.getenv("DRONE_DELIMITER");
        final var start = System.getenv("DRONE_START");
        final var end = System.getenv("DRONE_END");

        final var address = new InetSocketAddress(Constants.getDefaultHost(), port);
        final var format = new Drone.DataFormat(delimiter, start, end);
        final var drone = new Drone(address, 0, name, format);
        drone.run();

        return drone;
    }

}
