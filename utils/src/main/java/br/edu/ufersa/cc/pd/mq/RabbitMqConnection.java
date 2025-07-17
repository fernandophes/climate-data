package br.edu.ufersa.cc.pd.mq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import br.edu.ufersa.cc.pd.contracts.MqConnection;
import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.exceptions.MqConnectionException;
import br.edu.ufersa.cc.pd.exceptions.MqProducerException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RabbitMqConnection<T> implements MqConnection<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMqConnection.class.getSimpleName());
    private static final Gson GSON = new Gson();

    private final MqConnectionData data;
    private final Class<T> messageType;
    private final String queue;
    private final String exchange;
    private final String exchangeType;
    private final String routingKey;
    private final String dataModel;

    @Getter
    private Channel channel;
    private Connection connection;

    @Override
    public void createConnection() {
        LOG.info("Connecting to MQ at {}:{} with user {}", data.getHost(), data.getPort(), data.getUsername());

        final var factory = new ConnectionFactory();
        configureFactory(factory);

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchange, exchangeType, true);

            // Declare the queue and bind it to the exchange
            channel.queueDeclare(queue, true, false, false, null);
            if (!routingKey.isEmpty()) {
                channel.queueBind(queue, exchange, routingKey);
            } else {
                // For fanout exchanges, use empty routing key
                channel.queueBind(queue, exchange, "");
            }

            LOG.info("Queue '{}' declared and bound to exchange '{}'", queue, exchange);
        } catch (final IOException | TimeoutException e) {
            throw new MqConnectionException("Failed to create MQ connection", e);
        }
    }

    @Override
    public T receive() {
        try {
            LOG.info("Attempting to receive message from queue: {}", queue);

            // Use basicGet with a longer timeout approach
            // Try multiple times to catch messages that arrive between polls
            for (int attempt = 1; attempt <= 3; attempt++) {
                final var response = channel.basicGet(queue, true);

                if (response != null) {
                    final var messageAsString = new String(response.getBody());
                    LOG.info("Message received from queue '{}' on attempt {}: {}", queue, attempt, messageAsString);
                    LOG.info("Message envelope - delivery tag: {}, exchange: {}, routing key: {}",
                            response.getEnvelope().getDeliveryTag(),
                            response.getEnvelope().getExchange(),
                            response.getEnvelope().getRoutingKey());

                    return (T) messageAsString;
                }

                // If no message found, wait a bit before next attempt
                if (attempt < 3) {
                    Thread.sleep(50); // Short delay between attempts
                }
            }

            LOG.debug("No message available in queue: {} after 3 attempts", queue);
            return null;

        } catch (final IOException e) {
            LOG.info("Failed to receive message from queue '{}': {}", queue, e.getMessage(), e);
            throw new MqConnectionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Interrupted while receiving message from queue: {}", queue);
            return null;
        }
    }

    @Override
    public void send(final T message) {
        try {
            final var messageBytes = message.toString().getBytes(dataModel);

            channel.basicPublish(exchange, routingKey, null, messageBytes);
            LOG.info("Message sent: {}", message);
        } catch (final Exception e) {
            throw new MqProducerException("Failed to send message", e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            channel.close();
        } catch (TimeoutException e) {
            throw new IOException("Failed to close channel", e);
        }

        connection.close();
    }

    private void configureFactory(final ConnectionFactory factory) {
        factory.setHost(data.getHost());
        factory.setPort(data.getPort());
        factory.setVirtualHost("/");
        factory.setUsername(data.getUsername());
        factory.setPassword(data.getPassword());
    }

}
