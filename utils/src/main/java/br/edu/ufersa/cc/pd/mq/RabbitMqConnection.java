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
import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.exceptions.MqConnectionException;
import br.edu.ufersa.cc.pd.exceptions.MqProducerException;
import br.edu.ufersa.cc.pd.utils.JsonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RabbitMqConnection<T> implements MqConnection<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMqConnection.class.getSimpleName());

    private final MqConnectionData data;
    private final Class<T> messageType;
    private final String queue;
    private final String exchange;
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
        } catch (final IOException | TimeoutException e) {
            throw new MqConnectionException("Failed to create MQ connection", e);
        }
    }

    public void subscribe(final Consumer<T> consumer) {
        try {
            channel.basicConsume(queue, true, (tag, delivery) -> {
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

    public void subscribeLimited(final Consumer<T> consumer, int maxMessages) {
        try {
            channel.basicQos(maxMessages); // Limit prefetch to maxMessages
            final int[] count = { 0 };
            channel.basicConsume(queue, false, (tag, delivery) -> {
                if (count[0] < maxMessages) {
                    final var messageAsString = new String(delivery.getBody(), dataModel);
                    LOG.info("Mensagem recebida: {}", messageAsString);

                    T obj = JsonUtils.fromJson(messageAsString, messageType);
                    LOG.info("Mensagem convertida: {}", obj);

                    consumer.accept(obj);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    count[0]++;
                    if (count[0] >= maxMessages) {
                        channel.basicCancel(tag);
                    }
                }
            }, tag -> LOG.error("Leitura cancelada: {}", tag));
        } catch (IOException e) {
            throw new MqConnectionException(e);
        }
    }

    /**
     * Synchronously fetches and acknowledges up to maxMessages from the queue.
     */
    public void getLimited(final Consumer<T> consumer, int maxMessages) {
        try {
            for (int i = 0; i < maxMessages; i++) {
                var response = channel.basicGet(queue, false);
                if (response == null) {
                    break; // No more messages in the queue
                }
                final var messageAsString = new String(response.getBody(), dataModel);
                LOG.info("Mensagem recebida: {}", messageAsString);

                T obj = JsonUtils.fromJson(messageAsString, messageType);
                LOG.info("Mensagem convertida: {}", obj);

                consumer.accept(obj);
                channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
            }
        } catch (IOException e) {
            throw new MqConnectionException(e);
        }
    }

    @Override
    public void send(final T message) {
        try {
            final var json = JsonUtils.toJson(message);
            channel.basicPublish(exchange, routingKey, null, json.getBytes(dataModel));

            LOG.info("Message sent: {}", json);
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
