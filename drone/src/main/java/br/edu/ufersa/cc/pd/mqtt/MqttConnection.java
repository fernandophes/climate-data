package br.edu.ufersa.cc.pd.mqtt;

import br.edu.ufersa.cc.pd.envLoader.EnvLoader;
import br.edu.ufersa.cc.pd.mq.MqConnection;
import br.edu.ufersa.cc.pd.mq.MqProducer;
import com.rabbitmq.client.Channel;

public class MqttConnection<T> {
    private final String HOST = EnvLoader.getEnv("MQTT_HOST");
    private final int PORT = Integer.parseInt(EnvLoader.getEnv("MQTT_PORT"));
    private final String USERNAME = EnvLoader.getEnv("MQTT_USERNAME");
    private final String PASSWORD = EnvLoader.getEnv("MQTT_PASSWORD");
    private final String ROUTING_KEY = "climate_data.send";

    private String EXCHANGE;
    private String EXCHANGE_TYPE;
    private final MqConnection connection;
    private final Channel channel;

    public MqttConnection(String exchange, String exchangeType) {
        this.EXCHANGE = exchange;
        this.EXCHANGE_TYPE = exchangeType;

        this.connection = new MqConnection(HOST, PORT, USERNAME, PASSWORD, EXCHANGE, EXCHANGE_TYPE);
        this.channel = this.connection.createConnection();
    }

    public void sendMessage(T message) {
        MqProducer<T> producer = new MqProducer<>(this.channel, ROUTING_KEY, EXCHANGE, "UTF-8");
        producer.send(message);
    }
}
