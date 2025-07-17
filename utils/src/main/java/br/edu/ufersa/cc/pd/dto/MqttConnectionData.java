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
    // Real values from environment variables (commented out - using hardcoded
    // values):
    host = System.getenv("MQ_HOST");
    port = Integer.parseInt(System.getenv("MQTT_PORT")); // Note: should be MQ_PORT
    // not MQTT_PORT
    username = System.getenv("MQ_USERNAME");
    password = System.getenv("MQ_PASSWORD");

    // Mock data matching docker-compose.yml variables for MQTT:
    // host = "192.168.0.3"; // RabbitMQ container hostname
    // port = 1883; // MQTT port (not 5672 which is AMQP)
    // username = "drones"; // RabbitMQ admin user (not "drones")
    // password = "123456"; // RabbitMQ admin password (not "123456")

    // // Log the MQTT connection values for debugging
    // System.out.println("DEBUG - MQTT Host: " + host);
    // System.out.println("DEBUG - MQTT Port: " + port);
    // System.out.println("DEBUG - MQTT Username: " + username);
    // System.out.println("DEBUG - MQTT Password: " + password);
  }

}
