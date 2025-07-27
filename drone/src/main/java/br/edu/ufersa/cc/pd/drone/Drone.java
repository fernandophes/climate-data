package br.edu.ufersa.cc.pd.drone;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import br.edu.ufersa.cc.pd.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import br.edu.ufersa.cc.pd.utils.contracts.App;
import br.edu.ufersa.cc.pd.utils.dto.DataFormat;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import br.edu.ufersa.cc.pd.utils.dto.Snapshot;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Drone extends App {

    private static final long INTERVAL = 3_000;
    private static final Random RANDOM = new Random();
    private static final Timer TIMER = new Timer();
    private static final Gson GSON = new Gson();

    private String name;
    private DataFormat format;
    private final Logger logger;
    private final List<Consumer<DroneMessage>> callbacks = new ArrayList<>();

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
                final var snapshot = capture();
                final var formatted = snapshot.format(format);
                logger.info("Leitura feita: {}", formatted);

                final var message = new DroneMessage();
                message.setDroneName(name);
                message.setDataFormat(format);
                message.setMessage(formatted);

                callbacks.forEach(callback -> callback.accept(message));
            }
        };

        TIMER.schedule(subscription, Date.from(Instant.now()), INTERVAL);
    }

    @Override
    public void close() throws IOException {
        if (isRunning()) {
            subscription.cancel();
            logger.info("Atividade do drone {} finalizada", name);
        }
    }

    public void subscribe(final Consumer<DroneMessage> callback) {
        callbacks.add(callback);
    }

    public void unsubscribe(final Consumer<String> callback) {
        callbacks.remove(callback);
    }

    @Override
    public String getDescription() {
        final var example = new Snapshot(11.11, 22.22, 33.33, 44.44);

        return new StringBuilder()
                .append("Drone ").append(name)
                .append(" - Exemplo: ").append(example.format(format))
                .toString();
    }

    private Snapshot capture() {
        return new Snapshot(simulateValue(), simulateValue(), simulateValue(), simulateValue());
    }

    private double simulateValue() {
        final var asInt = RANDOM.nextInt(10_000);
        return asInt / 100d;
    }

}
