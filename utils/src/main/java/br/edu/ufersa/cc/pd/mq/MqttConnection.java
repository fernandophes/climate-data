package br.edu.ufersa.cc.pd.mq;

import java.io.IOException;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import br.edu.ufersa.cc.pd.contracts.MqConnection;
import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.dto.MqttConnectionData;
import br.edu.ufersa.cc.pd.exceptions.MqConnectionException;
import br.edu.ufersa.cc.pd.exceptions.MqProducerException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MqttConnection<T> implements MqConnection<T> {

  private static final Logger LOG = LoggerFactory.getLogger(MqttConnection.class.getSimpleName());
  private static final Gson GSON = new Gson();

  private final MqConnectionData data;
  private final Class<T> messageType;
  private final String topic;
  private final String clientId;

  @Getter
  private MqttClient client;
  private T lastReceivedMessage;

  public MqttConnection(MqConnectionData data, Class<T> messageType, String topic) {
    this.data = data;
    this.messageType = messageType;
    this.topic = topic;
    this.clientId = "mqtt-client-" + UUID.randomUUID().toString();
  }

  public MqttConnection(MqttConnectionData mqttData, Class<T> messageType, String topic) {
    this.data = new MqConnectionData(mqttData.getHost(), mqttData.getPort(),
        mqttData.getUsername(), mqttData.getPassword());
    this.messageType = messageType;
    this.topic = topic;
    this.clientId = "mqtt-client-" + UUID.randomUUID().toString();
  }

  @Override
  public void createConnection() {
    LOG.info("Connecting to MQTT broker at {}:{} with user {}", data.getHost(), data.getPort(), data.getUsername());

    try {
      final var brokerUrl = String.format("tcp://%s:%d", data.getHost(), data.getPort());
      client = new MqttClient(brokerUrl, clientId);

      final var options = new MqttConnectOptions();
      configureConnectionOptions(options);

      client.setCallback(new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
          LOG.warn("MQTT connection lost", cause);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
          final var messageAsString = new String(message.getPayload());
          lastReceivedMessage = GSON.fromJson(messageAsString, messageType);
          LOG.debug("Message received on topic {}: {}", topic, messageAsString);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
          LOG.debug("Message delivery complete: {}", token.getMessageId());
        }
      });

      client.connect(options);

      // Subscribe to the topic for receiving messages
      client.subscribe(topic);

      LOG.info("Connected to MQTT broker and subscribed to topic: {}", topic);

    } catch (final MqttException e) {
      throw new MqConnectionException("Failed to create MQTT connection", e);
    }
  }

  @Override
  public T receive() {
    if (!isConnected()) {
      throw new MqConnectionException("MQTT client is not connected");
    }

    // Return the last received message (this is a simple implementation)
    // For a more robust solution, you might want to use a queue or blocking
    // mechanism
    T message = lastReceivedMessage;
    lastReceivedMessage = null; // Clear after reading
    return message;
  }

  @Override
  public void send(T message) {
    if (!isConnected()) {
      throw new MqConnectionException("MQTT client is not connected");
    }

    try {
      final var messageJson = GSON.toJson(message);
      final var mqttMessage = new MqttMessage(messageJson.getBytes());
      mqttMessage.setQos(1); // At least once delivery

      client.publish(topic, mqttMessage);
      LOG.info("Message sent to topic {}: {}", topic, message);

    } catch (final MqttException e) {
      throw new MqProducerException("Failed to send MQTT message", e);
    }
  }

  @Override
  public void close() throws IOException {
    try {
      if (client != null && client.isConnected()) {
        client.disconnect();
        LOG.info("MQTT client disconnected");
      }
    } catch (final MqttException e) {
      throw new IOException("Failed to close MQTT connection", e);
    }
  }

  private void configureConnectionOptions(final MqttConnectOptions options) {
    options.setUserName(data.getUsername());
    options.setPassword(data.getPassword().toCharArray());
    options.setCleanSession(true);
    options.setAutomaticReconnect(true);
    options.setConnectionTimeout(10);
    options.setKeepAliveInterval(20);
  }

  private boolean isConnected() {
    return client != null && client.isConnected();
  }
}
