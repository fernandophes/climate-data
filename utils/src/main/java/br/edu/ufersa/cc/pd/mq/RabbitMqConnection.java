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
        } catch (final IOException | TimeoutException e) {
            throw new MqConnectionException("Failed to create MQ connection", e);
        }
    }

    @Override
    public T receive() {
        try {
            final var response = channel.basicGet(queue, true);
            final var messageAsString = new String(response.getBody());

            return (T) messageAsString;
        } catch (final IOException e) {
            throw new MqConnectionException(e);
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
