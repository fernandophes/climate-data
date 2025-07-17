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

    public MqConnectionData() {
        // Real values (commented out for testing):
        host = System.getenv("MQ_HOST");
        port = Integer.parseInt(System.getenv("MQ_PORT"));
        username = System.getenv("MQ_USERNAME");
        password = System.getenv("MQ_PASSWORD");

        // // Log the environment variables for debugging
        // System.out.println("DEBUG - MQ_HOST: " + host);
        // System.out.println("DEBUG - MQ_PORT: " + System.getenv("MQ_PORT"));
        // System.out.println("DEBUG - MQ_USERNAME: " + username);
        // System.out.println("DEBUG - MQ_PASSWORD: " + password);

        // Mocked values for testing:
        // host = "192.168.0.3";
        // port = 5672;
        // username = "drones";
        // password = "123456";
    }

}
