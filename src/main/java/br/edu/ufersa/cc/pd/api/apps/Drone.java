package br.edu.ufersa.cc.pd.api.apps;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import br.edu.ufersa.cc.pd.api.contracts.Executable;
import br.edu.ufersa.cc.pd.api.dto.Snapshot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = true)
public class Drone extends Executable {

    @Getter
    @AllArgsConstructor
    public static class DataFormat {
        private final String delimiter;
        private final String before;
        private final String after;

        public DataFormat(final String delimiter) {
            this.delimiter = delimiter;
            before = "";
            after = "";
        }
    }

    private static final long INTERVAL = 3_000;
    private static final Random RANDOM = new Random();
    private static final Timer TIMER = new Timer();

    private String name;
    private DataFormat format;

    private TimerTask subscription;

    public Drone(final InetSocketAddress address, final int port, final String name, final DataFormat format) {
        super(address, port);
        this.name = name;
        this.format = format;
    }

    @Override
    public boolean isRunning() {
        return subscription != null;
    }

    @Override
    public void run() {
        subscription = new TimerTask() {
            @Override
            public void run() {
                System.out.println(capture().format(format));
            }
        };

        TIMER.schedule(subscription, Date.from(Instant.now()), INTERVAL);
    }

    @Override
    public void close() throws IOException {
        if (isRunning()) {
            subscription.cancel();
        }
    }

    public Snapshot capture() {
        return new Snapshot(RANDOM.nextDouble(), RANDOM.nextDouble(), RANDOM.nextDouble(), RANDOM.nextDouble());
    }

    @Override
    public String getDescription() {
        final var example = new Snapshot(1111.11, 2222.22, 3333.33, 4444.44);

        return new StringBuilder()
                .append("Drone ").append(name)
                .append(" - Exemplo: ").append(example.format(format))
                .toString();
    }

}
