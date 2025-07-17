package br.edu.ufersa.cc.pd;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.rabbitmq.client.CancelCallback;
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
public class RabbitMqConnection2<T> implements MqConnection<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMqConnection2.class.getSimpleName());
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
            LOG.info("Aguardando mensagem...");
            final var response = channel.basicGet(String.join(".", exchange, queue), true);
            LOG.info("Response lido: {}", response);
            final var messageAsString = new String(response.getBody());
            LOG.info("Mensagem lida (string): {}", messageAsString);

            T obj = GSON.fromJson(messageAsString, messageType);
            LOG.info("Mensagem convertida: {}", obj);
            return obj;
        } catch (final IOException e) {
            throw new MqConnectionException(e);
        }
    }

    public String subscribe(final Consumer<T> consumer) throws IOException {
        return channel.basicConsume(String.join(".", exchange, queue), true, (tag, delivery) -> {
            final var messageAsString = new String(delivery.getBody(), dataModel);
            LOG.info("Mensagem recebida: {}", messageAsString);

            T obj = GSON.fromJson(messageAsString, messageType);
            LOG.info("Mensagem convertida: {}", obj);

            consumer.accept(obj);
        }, tag -> LOG.error("Leitura cancelada: {}", tag));
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
