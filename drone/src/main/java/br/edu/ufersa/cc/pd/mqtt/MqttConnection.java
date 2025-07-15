package br.edu.ufersa.cc.pd.mqtt;

import br.edu.ufersa.cc.pd.envLoader.EnvLoader;
import br.edu.ufersa.cc.pd.mq.MqConnection;
import br.edu.ufersa.cc.pd.mq.MqProducer;
import com.rabbitmq.client.Channel;
import com.google.gson.JsonObject;

public class MqttConnection<T> {

    private static final String HOST = EnvLoader.getEnv("MQTT_HOST");
    private static final int PORT = Integer.parseInt(EnvLoader.getEnv("MQTT_PORT"));
    private static final String USERNAME = EnvLoader.getEnv("MQTT_USERNAME");
    private static final String PASSWORD = EnvLoader.getEnv("MQTT_PASSWORD");
    
    private final MqConnection connection;
    private final Channel channel;
    private final String routingKey;
    private final String exchange;

    public MqttConnection(final String exchange, final String exchangeType, final String routingKey) {
        this.exchange = exchange;
        this.routingKey = routingKey;

        this.connection = new MqConnection(HOST, PORT, USERNAME, PASSWORD, exchange, exchangeType);
        this.channel = this.connection.createConnection();
    }

    public void sendMessage(final T message) {
        final var producer = new MqProducer<>(this.channel, routingKey, exchange, "UTF-8");
        producer.send(message);
    }

}
