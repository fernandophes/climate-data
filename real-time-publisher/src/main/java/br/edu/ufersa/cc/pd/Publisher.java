package br.edu.ufersa.cc.pd;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.contracts.MqProducer;
import br.edu.ufersa.cc.pd.contracts.MqSubscriber;
import br.edu.ufersa.cc.pd.utils.contracts.App;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class Publisher extends App {

    private static final Logger LOG = LoggerFactory.getLogger(Publisher.class.getSimpleName());

    /*
     * Conexões MQ
     */
    private final MqSubscriber<DroneMessage> consumer;
    private final MqProducer<DroneMessage> onDemandProducer;
    private final MqProducer<DroneMessage> realTimeProducer;

    @Getter
    private boolean running = false;

    public Publisher(final int port, final MqSubscriber<DroneMessage> consumer,
            final MqProducer<DroneMessage> realTimeProducer, final MqProducer<DroneMessage> onDemandProducer) {
        super(null, port);
        this.consumer = consumer;
        this.realTimeProducer = realTimeProducer;
        this.onDemandProducer = onDemandProducer;
    }

    @Override
    public void run() {
        running = true;

        LOG.info("Inscrevendo-se para ler a fila vinda do gateway...", isRunning());
        consumer.subscribe(message -> {
            realTimeProducer.send(message);
            onDemandProducer.send(message);
        });
        LOG.info("Inscrito!", isRunning());
    }

    @Override
    public void close() throws IOException {
        running = false;

        // Close MQTT connection
        try {
            consumer.close();
            realTimeProducer.close();
            onDemandProducer.close();
        } catch (final Exception e) {
            LOG.error("Error closing MQTT connection", e);
        }
    }

    @Override
    public String getDescription() {
        return "Publisher";
    }

}
