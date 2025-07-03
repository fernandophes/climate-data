package br.edu.ufersa.cc.pd;

import java.util.List;
import java.util.Scanner;

import br.edu.ufersa.cc.pd.api.contracts.Launcher;
import br.edu.ufersa.cc.pd.api.launchers.DroneLauncher;

public class Main {

    private static final List<Launcher<?>> LAUNCHERS = List.of(
            new DroneLauncher());

    public static void main(String[] args) {
        listLaunchers();

        final var scanner = new Scanner(System.in);

        var keepRunning = true;
        do {
            final var selected = scanner.nextInt();
            final var index = selected - 1;

            try {
                LAUNCHERS.get(index).launch();
                keepRunning = false;
            } catch (final IndexOutOfBoundsException e) {
                if (selected == 0) {
                    System.out.println("Saindo...");
                    keepRunning = false;
                } else {
                    System.out.println("A opção " + selected + " não é válida");
                    System.out.println();
                    listLaunchers();
                }
            }

        } while (keepRunning);

        scanner.close();
    }

    private static void listLaunchers() {
        System.out.println("Qual aplicação você deseja iniciar?");

        for (var i = 0; i < LAUNCHERS.size(); i++) {
            final var launcher = LAUNCHERS.get(i);

            System.out.println((i + 1) + ". " + launcher.getName());
        }

        System.out.println("0. Sair");
        System.out.println();
    }

}
