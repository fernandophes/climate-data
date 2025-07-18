package br.edu.ufersa.cc.pd.mq;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.contracts.MqConnection;
import br.edu.ufersa.cc.pd.contracts.MqSubscriber;
import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.dto.MqttConnectionData;
import br.edu.ufersa.cc.pd.exceptions.MqConnectionException;
import br.edu.ufersa.cc.pd.exceptions.MqProducerException;
import br.edu.ufersa.cc.pd.utils.JsonUtils;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MqttConnection<T> implements MqConnection<T>, MqSubscriber<T> {

  private static final Logger LOG = LoggerFactory.getLogger(MqttConnection.class.getSimpleName());

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
    this.clientId = "mqtt-client-" + UUID.randomUUID().toString(); // Generate unique client ID
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
          lastReceivedMessage = JsonUtils.fromJson(messageAsString, messageType);
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

    T message = lastReceivedMessage;
    lastReceivedMessage = null;
    return message;
  }

  @Override
  public String subscribe(Consumer<T> consumer) {
    client.setCallback(new MqttCallback() {
      @Override
      public void connectionLost(Throwable cause) {
        LOG.warn("MQTT connection lost", cause);
      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        final var messageAsString = new String(message.getPayload());
        final var object = JsonUtils.fromJson(messageAsString, messageType);
        consumer.accept(object);
        LOG.debug("Message received on topic {}: {}", topic, messageAsString);
      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken token) {
        LOG.debug("Message delivery complete: {}", token.getMessageId());
      }
    });

    return "Ok";
  }

  @Override
  public void send(T message) {
    if (!isConnected()) {
      throw new MqConnectionException("MQTT client is not connected");
    }

    try {
      final var messageJson = JsonUtils.toJson(message);
      final var mqttMessage = new MqttMessage(messageJson.getBytes());
      mqttMessage.setQos(1); // At least once delivery

      client.publish(topic, mqttMessage);
      LOG.info("Message sent to topic {}: {}", topic, message);

    } catch (final MqttException e) {
      throw new MqProducerException("Failed to send MQTT message", e);
    }
  }

  // New method to publish to a specific topic using the same connection
  public void sendToTopic(String targetTopic, DroneMessage message) {
    try {
      if (!isConnected()) {
        throw new MqConnectionException("MQTT client is not connected");
      }

      final var json = JsonUtils.toJson(message);
      final var mqttMessage = new MqttMessage(json.getBytes());
      mqttMessage.setQos(1); // At least once delivery

      client.publish(targetTopic, mqttMessage);
      LOG.info("Message sent to topic {}: {}", targetTopic, message);

    } catch (final MqttException e) {
      throw new MqProducerException("Failed to send MQTT message to topic: " + targetTopic, e);
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
