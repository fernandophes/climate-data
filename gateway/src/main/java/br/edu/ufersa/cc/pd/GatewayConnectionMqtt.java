package br.edu.ufersa.cc.pd;

import br.edu.ufersa.cc.pd.dto.MqttConnectionData;
import br.edu.ufersa.cc.pd.mq.MqttConnection;

public class GatewayConnectionMqtt extends MqttConnection<String> {

  public GatewayConnectionMqtt(final String topic) {
    super(new MqttConnectionData(), String.class, topic);
  }

  public GatewayConnectionMqtt(final MqttConnectionData data, final String topic) {
    super(data, String.class, topic);
  }

  // Wrapper method to expose sendToTopic functionality
  public void sendToTopic(String topic, String message) {
    super.sendToTopic(topic, message);
  }
}
