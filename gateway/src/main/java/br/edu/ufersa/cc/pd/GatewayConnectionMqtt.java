package br.edu.ufersa.cc.pd;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.mq.MqttConnection;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;

public class GatewayConnectionMqtt extends MqttConnection<DroneMessage> {

  public GatewayConnectionMqtt(final String topic) {
    super(MqConnectionData.mqtt(), DroneMessage.class, topic);
  }

  public GatewayConnectionMqtt(final MqConnectionData data, final String topic) {
    super(data, DroneMessage.class, topic);
  }

  // Wrapper method to expose sendToTopic functionality
  public void sendToTopic(String topic, String message) {
    super.sendToTopic(topic, message);
  }
}
