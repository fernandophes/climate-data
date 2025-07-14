package br.edu.ufersa.cc.pd.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MqConnection {
    private final String HOST;
    private final int PORT;
    private final String USERNAME;
    private final String PASSWORD;
    private final String EXCHANGE;
    private final String EXCHANGE_TYPE;

    public MqConnection(String host, int port, String username, String password, String exchange, String exchangeType) {
        this.HOST = host;
        this.PORT = port;
        this.USERNAME = username;
        this.PASSWORD = password;
        this.EXCHANGE = exchange;
        this.EXCHANGE_TYPE = exchangeType;
    }

    public Channel createConnection() {
        System.out.println("Connecting to MQ at " + HOST + ":" + PORT + " with user " + USERNAME);
        ConnectionFactory factory = new ConnectionFactory();
        configureFactory(factory);

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE, true);

            return channel;
        } catch (IOException | TimeoutException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            throw new RuntimeException("Failed to create MQ connection", e);
        }

    }

    public void configureFactory(ConnectionFactory factory) {
        factory.setHost(this.HOST);
        factory.setPort(this.PORT);
        factory.setVirtualHost("/");
        factory.setUsername(this.USERNAME);
        factory.setPassword(this.PASSWORD);
    }
}
