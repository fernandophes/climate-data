package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;

public class RealTimeConnection extends MqttConnection<DroneMessage> {

  public RealTimeConnection(final String topic) {
    super(MqConnectionData.mqtt(), DroneMessage.class, topic);
  }

  public RealTimeConnection(final MqConnectionData data, final String topic) {
    super(data, DroneMessage.class, topic);
  }

}
