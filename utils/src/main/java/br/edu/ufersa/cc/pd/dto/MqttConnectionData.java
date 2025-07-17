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
    host = System.getenv("MQTT_HOST") != null ? System.getenv("MQTT_HOST") : System.getenv("MQ_HOST");
    port = System.getenv("MQTT_PORT") != null ? Integer.parseInt(System.getenv("MQTT_PORT")) : 1883;
    username = System.getenv("MQTT_USERNAME") != null ? System.getenv("MQTT_USERNAME") : System.getenv("MQ_USERNAME");
    password = System.getenv("MQTT_PASSWORD") != null ? System.getenv("MQTT_PASSWORD") : System.getenv("MQ_PASSWORD");
  }

}
