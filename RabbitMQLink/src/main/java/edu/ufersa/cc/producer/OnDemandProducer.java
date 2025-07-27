package edu.ufersa.cc.producer;

import br.edu.ufersa.cc.pd.contracts.MqProducer;
import br.edu.ufersa.cc.pd.contracts.MqSubscriber;
import br.edu.ufersa.cc.pd.utils.contracts.App;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

import java.io.IOException;

@EqualsAndHashCode(callSuper = true)
public class OnDemandProducer extends App {

    private final MqProducer<DroneMessage> mqProducer;
    private final MqSubscriber<DroneMessage> mqSubscriber;
    private static final Gson GSON = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(OnDemandProducer.class.getSimpleName());

    public OnDemandProducer(final int port, MqSubscriber<DroneMessage> mqSubscriber, MqProducer<DroneMessage> mqProducer) {
        super(null, port);
        this.mqProducer = mqProducer;
        this.mqSubscriber = mqSubscriber;
    }

    @Getter
    private boolean running = false;

    @Override
    public String getDescription() {
        return "RabbitMQ Link On-Demand Consumer/Producer";
    }

    @Override
    public void close() throws IOException {
        running = false;
    }

    @Override
    public void run() {
        LOG.info("Running Gateway - isRunning(): {} - port: {}", isRunning(), getPort());
        running = true;

        LOG.info("Inscrevendo-se para ler a fila dos drones...", isRunning());
        mqSubscriber.subscribe(message -> {
            publishToFinalQueue(message);
        });
        LOG.info("Inscrito!", isRunning());

        LOG.info("Gateway main loop exited - isRunning(): {}", isRunning());
    }

    private void publishToFinalQueue(final DroneMessage message) {
        try {
            if (mqProducer != null) {
                mqProducer.send(message);
                LOG.info("Published drone message to producer queue for client on demand");
            } else {
                LOG.warn("Producer connection not available, skipping message publication for client on demand");
            }
        } catch (final Exception e) {
            LOG.error("Failed to publish to producer queue for client on demand");
        }
    }
}
