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
        final var host = System.getenv("MQ_HOST");
        final var port = Integer.parseInt(System.getenv("MQ_PORT"));
        final var username = System.getenv("MQ_USERNAME");
        final var password = System.getenv("MQ_PASSWORD");

        return new MqConnectionData(host, port, username, password);
    }

    public static MqConnectionData mqtt() {
        final var host = System.getenv("MQ_HOST");
        final var port = Integer.parseInt(System.getenv("MQTT_PORT"));
        final var username = System.getenv("MQ_USERNAME");
        final var password = System.getenv("MQ_PASSWORD");

        return new MqConnectionData(host, port, username, password);
    }

}
