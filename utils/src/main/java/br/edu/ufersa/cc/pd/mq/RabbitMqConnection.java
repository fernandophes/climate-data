package br.edu.ufersa.cc.pd.mq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import br.edu.ufersa.cc.pd.contracts.MqConnection;
import br.edu.ufersa.cc.pd.contracts.MqSubscriber;
import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.exceptions.MqConnectionException;
import br.edu.ufersa.cc.pd.exceptions.MqProducerException;
import br.edu.ufersa.cc.pd.utils.JsonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RabbitMqConnection<T> implements MqConnection<T>, MqSubscriber<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMqConnection.class.getSimpleName());

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

            LOG.info("Queue '{}' declared and bound to exchange '{}'", queue, exchange);
        } catch (final IOException | TimeoutException e) {
            throw new MqConnectionException("Failed to create MQ connection", e);
        }
    }

    @Override
    public T receive() {
        try {
            LOG.info("Attempting to receive message from queue: {}", queue);
            final var response = channel.basicGet(exchange + "." + queue, true);

            if (response == null) {
                LOG.debug("No message available in queue: {}", queue);
                return null;
            }

            final var messageAsString = new String(response.getBody());

            return (T) messageAsString;
        } catch (final IOException e) {
            LOG.info("Failed to receive message from queue '{}': {}", queue, e.getMessage(), e);
            throw new MqConnectionException(e);
        }
    }

    public String subscribe(final Consumer<T> consumer) {
        try {
            return channel.basicConsume(String.join(".", exchange, queue), true, (tag, delivery) -> {
                final var messageAsString = new String(delivery.getBody(), dataModel);
                LOG.info("Mensagem recebida: {}", messageAsString);

                T obj = JsonUtils.fromJson(messageAsString, messageType);
                LOG.info("Mensagem convertida: {}", obj);

                consumer.accept(obj);
            }, tag -> LOG.error("Leitura cancelada: {}", tag));
        } catch (IOException e) {
            throw new MqConnectionException(e);
        }
    }

    @Override
    public void send(final T message) {
        try {
            final var messageJson = JsonUtils.toJson(message);

            channel.basicPublish(exchange, routingKey, null, messageJson.getBytes(dataModel));
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
