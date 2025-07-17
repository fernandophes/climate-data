package br.edu.ufersa.cc.pd.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MqttConnectionData implements Serializable {

  private final String host;
  private final int port;
  private final String username;
  private final String password;

  public MqttConnectionData() {
    // Real values from environment variables:
    host = System.getenv("MQ_HOST");
    port = Integer.parseInt(System.getenv("MQTT_PORT"));
    username = System.getenv("MQ_USERNAME");
    password = System.getenv("MQ_PASSWORD");

    // Mock data matching docker-compose.yml variables:
    // host = "192.168.0.3";
    // port = 1883;
    // username = "drones";
    // password = "123456";
  }

}
