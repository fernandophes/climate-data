package br.edu.ufersa.cc.pd.api.apps;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.api.contracts.App;
import br.edu.ufersa.cc.pd.api.dto.Snapshot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = true)
public class Drone extends App {

    @Getter
    @AllArgsConstructor
    public static class DataFormat {
        private final String delimiter;
        private final String start;
        private final String end;

        public DataFormat(final String delimiter) {
            this.delimiter = delimiter;
            start = "";
            end = "";
        }
    }

    private static final long INTERVAL = 3_000;
    private static final Random RANDOM = new Random();
    private static final Timer TIMER = new Timer();

    private String name;
    private DataFormat format;
    private final Logger logger;

    private TimerTask subscription;

    public Drone(final InetSocketAddress address, final int port, final String name, final DataFormat format) {
        super(address, port);
        this.name = name;
        this.format = format;

        logger = LoggerFactory.getLogger("Drone " + name);
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
                logger.info("Leitura feita: {}", capture().format(format));
            }
        };

        TIMER.schedule(subscription, Date.from(Instant.now()), INTERVAL);
    }

    @Override
    public void close() throws IOException {
        if (isRunning()) {
            subscription.cancel();
            logger.info("Atividade do drone finalizada", name);
        }
    }

    public Snapshot capture() {
        return new Snapshot(simulateValue(), simulateValue(), simulateValue(), simulateValue());
    }

    @Override
    public String getDescription() {
        final var example = new Snapshot(11.11, 22.22, 33.33, 44.44);

        return new StringBuilder()
                .append("Drone ").append(name)
                .append(" - Exemplo: ").append(example.format(format))
                .toString();
    }

    private double simulateValue() {
        final var asInt = RANDOM.nextInt(10_000);
        return asInt / 100d;
    }

}
