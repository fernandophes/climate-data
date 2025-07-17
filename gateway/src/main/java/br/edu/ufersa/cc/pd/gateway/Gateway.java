package br.edu.ufersa.cc.pd.gateway;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.GatewayConnectionMqtt;
import br.edu.ufersa.cc.pd.contracts.MqSubscriber;
import br.edu.ufersa.cc.pd.entities.Capture;
import br.edu.ufersa.cc.pd.services.CaptureService;
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
    private final GatewayConnectionMqtt mqttConnection;
    private final CaptureService captureService = new CaptureService();

    @Getter
    private boolean running = false;

    public Gateway(final int port, final MqSubscriber<DroneMessage> consumer,
            final GatewayConnectionMqtt mqttConnection) {
        super(null, port);
        this.mqSubscriber = consumer;
        this.mqttConnection = mqttConnection;
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

    private void publishToMqttBroker(final DroneMessage message) {
        final var region = message.getDroneName();

        try {
            // Use the single MQTT connection instance
            if (mqttConnection != null && mqttConnection.getClient() != null
                    && mqttConnection.getClient().isConnected()) {
                final var topic = "climate_data." + region;

                // Send to the specific topic using the existing connection
                mqttConnection.sendToTopic(topic, message);

                LOG.info("Published climate data to MQTT topic '{}': {}", topic, message);
            } else {
                LOG.warn("MQTT connection not available, skipping message publication for region: {}", region);
            }
        } catch (final Exception e) {
            LOG.error("Failed to publish to MQTT broker for region: {}", region, e);
        }
    }

    @Override
    public void run() {
        LOG.info("Running Gateway - isRunning(): {} - port: {}", isRunning(), getPort());
        running = true;

        // try {
        // // Initialize single MQTT connection for reuse
        // mqttConnection.createConnection();
        // LOG.info("MQTT connection initialized successfully");

        // } catch (final Exception e) {
        // LOG.error("Failed to initialize MQTT connection", e);
        // running = false;
        // return;
        // }

        LOG.info("Inscrevendo-se para ler a fila dos drones...", isRunning());
        mqSubscriber.subscribe(message -> {
            saveDataInDatabase(message);
            publishToMqttBroker(message);
        });
        LOG.info("Inscrito!", isRunning());

        LOG.info("Gateway main loop exited - isRunning(): {}", isRunning());
    }

    @Override
    public void close() throws IOException {
        running = false;

        // Close MQTT connection
        try {
            if (mqttConnection != null) {
                mqttConnection.close();
                LOG.info("MQTT connection closed");
            }
        } catch (final Exception e) {
            LOG.error("Error closing MQTT connection", e);
        }
    }

    @Override
    public String getDescription() {
        return "Gateway";
    }

}
