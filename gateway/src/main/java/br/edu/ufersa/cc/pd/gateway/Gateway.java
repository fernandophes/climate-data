package br.edu.ufersa.cc.pd.gateway;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.GatewayConnectionMqtt;
import br.edu.ufersa.cc.pd.contracts.MqProducer;
import br.edu.ufersa.cc.pd.contracts.MqSubscriber;
import br.edu.ufersa.cc.pd.entities.Capture;
import br.edu.ufersa.cc.pd.services.CaptureService;
import br.edu.ufersa.cc.pd.utils.JsonUtils;
import br.edu.ufersa.cc.pd.utils.contracts.App;
import br.edu.ufersa.cc.pd.utils.dto.DataFormat;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import br.edu.ufersa.cc.pd.utils.dto.Snapshot;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class Gateway extends App {

    private static final Logger LOG = LoggerFactory.getLogger(Gateway.class.getSimpleName());

    private final MqSubscriber<DroneMessage> mqSubscriber;
    private final MqProducer<DroneMessage> onDemand;
    private final CaptureService captureService = new CaptureService();

    @Getter
    private boolean running = false;

    public Gateway(final int port, final MqSubscriber<DroneMessage> consumer,
            final MqProducer<DroneMessage> onDemand) {
        super(null, port);
        this.mqSubscriber = consumer;
        this.onDemand = onDemand;
    }

    private void saveDataInDatabase(final DroneMessage message) {
        final var region = message.getDroneName();
        final var format = message.getDataFormat();
        final var formatted = message.getMessage();
        final var snapshot = Snapshot.from(formatted, format);

        final var dbFormat = new DataFormat(" | ", "[", "]");

        final var capture = new Capture();
        capture.setWeatherData(snapshot.format(dbFormat));
        capture.setRegion(region);

        captureService.create(capture);
    }

    private void publishToProducerToClientOnDemandQueue(final DroneMessage message) {
        try {
            if (onDemand != null) {
                onDemand.send(message);
                LOG.info("Published drone message to producer queue for client on demand");
            } else {
                LOG.warn("Producer connection not available, skipping message publication for client on demand");
            }
        } catch (final Exception e) {
            LOG.error("Failed to publish to producer queue for client on demand");
        }
    }

    @Override
    public void run() {
        LOG.info("Running Gateway - isRunning(): {} - port: {}", isRunning(), getPort());
        running = true;

        LOG.info("Inscrevendo-se para ler a fila dos drones...", isRunning());
        mqSubscriber.subscribe(message -> {
            saveDataInDatabase(message);
            publishToProducerToClientOnDemandQueue(message);
        });
        LOG.info("Inscrito!", isRunning());

        LOG.info("Gateway main loop exited - isRunning(): {}", isRunning());
    }

    @Override
    public void close() throws IOException {
        running = false;
    }

    @Override
    public String getDescription() {
        return "Gateway";
    }

}
