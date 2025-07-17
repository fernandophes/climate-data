package br.edu.ufersa.cc.pd.gateway;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import br.edu.ufersa.cc.pd.GatewayConnectionMqtt;
import br.edu.ufersa.cc.pd.contracts.MqConsumer;
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

    private static final Gson GSON = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(Gateway.class.getSimpleName());

    private final MqConsumer<String> consumer;
    private final GatewayConnectionMqtt mqttConnection;
    private final CaptureService captureService = new CaptureService();

    @Getter
    private boolean running = false;

    public Gateway(final int port, final MqConsumer<String> consumer, final GatewayConnectionMqtt mqttConnection) {
        super(null, port);
        this.consumer = consumer;
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

        // Publish to MQTT broker after saving to database
        publishToMqttBroker(region, snapshot.format(format));
    }

    private void publishToMqttBroker(final String region, final String weatherData) {
        try {
            final var topic = "climate_data." + region;

            // Create a proper JSON object
            final var messageData = new ClimateMessage(region, weatherData, System.currentTimeMillis());
            final var message = GSON.toJson(messageData);

            // Create a new MQTT connection for this specific region/topic
            final var regionMqttConnection = new GatewayConnectionMqtt(topic);
            regionMqttConnection.createConnection();
            regionMqttConnection.send(message);
            regionMqttConnection.close();

            LOG.info("Published climate data to MQTT topic '{}': {}", topic, message);
        } catch (final Exception e) {
            LOG.error("Failed to publish to MQTT broker for region: {}", region, e);
        }
    }

    // Inner class to represent the MQTT message structure
    private static class ClimateMessage {
        private final String region;
        private final String data;
        private final long timestamp;

        public ClimateMessage(String region, String data, long timestamp) {
            this.region = region;
            this.data = data;
            this.timestamp = timestamp;
        }

        public String getRegion() {
            return region;
        }

        public String getData() {
            return data;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    @Override
    public void run() {
        LOG.info("Running Gateway - isRunning(): {} - port: {}", isRunning(), getPort());
        running = true;

        try {
            // Initialize database
            captureService.initialize();
            LOG.info("Database initialized successfully");

            // Initialize MQTT connection
            mqttConnection.createConnection();
            LOG.info("MQTT connection initialized successfully");

        } catch (final SQLException e) {
            LOG.error("Failed to initialize database", e);
            running = false;
            return;
        } catch (final Exception e) {
            LOG.error("Failed to initialize MQTT connection", e);
            running = false;
            return;
        }

        while (isRunning()) {
            LOG.info("Running Gateway LOOP - checking for messages...");
            final var json = consumer.receive();
            LOG.info("Raw JSON received: {}", json);
            if (json != null) {
                LOG.info("Raw JSON received: {}", json);
                try {
                    // Parse the JSON string as DroneMessage
                    LOG.info("Parsing JSON message to DroneMessage object");
                    final var droneMessage = GSON.fromJson(json, DroneMessage.class);
                    LOG.info("Successfully parsed drone message from region: {}", droneMessage.getDroneName());

                    saveDataInDatabase(droneMessage);
                    LOG.info("Successfully processed message from drone: {}", droneMessage.getDroneName());
                } catch (Exception e) {
                    LOG.info("Failed to parse JSON message: {}", json, e);
                }
            } else {
                LOG.info("No message available from consumer, sleeping for 1 second...");
                // No message available, sleep briefly to avoid busy waiting
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOG.info("Gateway thread interrupted during sleep");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        LOG.info("Gateway main loop exited - isRunning(): {}", isRunning());
    }

    @Override
    public void close() throws IOException {
        running = false;

        // Close MQTT connection
        try {
            mqttConnection.close();
            LOG.info("MQTT connection closed");
        } catch (final Exception e) {
            LOG.error("Error closing MQTT connection", e);
        }
    }

    @Override
    public String getDescription() {
        return "Gateway";
    }

}
