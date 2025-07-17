package br.edu.ufersa.cc.pd.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MqConnectionData implements Serializable {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public static MqConnectionData rabbitMq() {
        // final var host = System.getenv("MQ_HOST");
        // final var port = Integer.parseInt(System.getenv("MQ_PORT"));
        // final var username = System.getenv("MQ_USERNAME");
        // final var password = System.getenv("MQ_PASSWORD");

        final var host = "192.168.0.3"; // RabbitMQ container hostname
        final var port = 5672; // MQTT port (not 5672 which is AMQP)
        final var username = "drones"; // RabbitMQ admin user (not "drones")
        final var password = "123456"; // RabbitMQ admin password (not "123456")

        return new MqConnectionData(host, port, username, password);
    }

    public static MqConnectionData mqtt() {
        // final var host = System.getenv("MQ_HOST");
        // final var port = Integer.parseInt(System.getenv("MQTT_PORT"));
        // final var username = System.getenv("MQ_USERNAME");
        // final var password = System.getenv("MQ_PASSWORD");

        final var host = "192.168.0.3"; // RabbitMQ container hostname
        final var port = 1883; // MQTT port (not 5672 which is AMQP)
        final var username = "drones"; // RabbitMQ admin user (not "drones")
        final var password = "123456"; // RabbitMQ admin password (not "123456")

        return new MqConnectionData(host, port, username, password);
    }

}
