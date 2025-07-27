package br.edu.ufersa.cc.pd.mq;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.contracts.MqConnection;
import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.exceptions.MqConnectionException;
import br.edu.ufersa.cc.pd.exceptions.MqProducerException;
import br.edu.ufersa.cc.pd.utils.JsonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MqttConnection<T> implements MqConnection<T> {

  private static final Logger LOG = LoggerFactory.getLogger(MqttConnection.class.getSimpleName());

  private final MqConnectionData credentials;
  private final Class<T> messageType;
  private final Function<T, String> topicSendMapper;
  private final Supplier<String> topicReadSupplier;
  private final String clientId = "mqtt-client-" + UUID.randomUUID().toString();

  @Getter
  private MqttClient client;

  @Override
  public void createConnection() {
    LOG.info("Connecting to MQTT broker at {}:{} with user {}", credentials.getHost(), credentials.getPort(),
        credentials.getUsername());

    try {
      final var brokerUrl = String.format("tcp://%s:%d", credentials.getHost(), credentials.getPort());
      client = new MqttClient(brokerUrl, clientId);


      client.setCallback(new MqttCallback() {
        @Override
        public void connectionLost(final Throwable cause) {
          LOG.warn("MQTT connection lost", cause);
        }

        @Override
        public void messageArrived(final String topic, final MqttMessage message) throws Exception {
          final var messageAsString = new String(message.getPayload());
          LOG.debug("Message received on topic {}: {}", topic, messageAsString);
        }

        @Override
        public void deliveryComplete(final IMqttDeliveryToken token) {
          LOG.debug("Message delivery complete: {}", token.getMessageId());
        }
      });

      client.connect(configureConnectionOptions());
      LOG.info("Connected to MQTT broker and subscribed to topic: {}", topicSendMapper);
    } catch (final MqttException e) {
      throw new MqConnectionException("Failed to create MQTT connection", e);
    }
  }

  @Override
  public void subscribe(final Consumer<T> consumer) {
    try {
      client.subscribe(topicReadSupplier.get());

      client.setCallback(new MqttCallback() {
        @Override
        public void connectionLost(final Throwable cause) {
          LOG.warn("MQTT connection lost", cause);
        }

        @Override
        public void messageArrived(final String topic, final MqttMessage message) throws Exception {
          final var messageAsString = new String(message.getPayload());
          final var object = JsonUtils.fromJson(messageAsString, messageType);
          consumer.accept(object);
          LOG.debug("Message received on topic {}: {}", topic, messageAsString);
        }

        @Override
        public void deliveryComplete(final IMqttDeliveryToken token) {
          LOG.debug("Message delivery complete: {}", token.getMessageId());
        }
      });
    } catch (final MqttException e) {
      LOG.error("Erro ao inscrever-se no tópico", e);
    }
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

      final var topic = topicSendMapper.apply(message);

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

  private MqttConnectOptions configureConnectionOptions() {
    final var options = new MqttConnectOptions();

    options.setUserName(credentials.getUsername());
    options.setPassword(credentials.getPassword().toCharArray());
    options.setCleanSession(true);
    options.setAutomaticReconnect(true);
    options.setConnectionTimeout(10);
    options.setKeepAliveInterval(20);

    return options;
  }

  private boolean isConnected() {
    return client != null && client.isConnected();
  }

}
