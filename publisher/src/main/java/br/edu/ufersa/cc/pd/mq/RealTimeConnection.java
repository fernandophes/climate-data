package br.edu.ufersa.cc.pd.mq;

import java.util.function.Function;
import java.util.function.Supplier;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;

public class RealTimeConnection extends MqttConnection<DroneMessage> {

  public RealTimeConnection(final MqConnectionData data, final Function<DroneMessage, String> topicSendMapper,
      final Supplier<String> topicReadSupplier) {
    super(data, DroneMessage.class, topicSendMapper, topicReadSupplier);
  }

  public RealTimeConnection(final Function<DroneMessage, String> topicSendMapper,
      final Supplier<String> topicReadSupplier) {
    this(MqConnectionData.mqtt(), topicSendMapper, topicReadSupplier);
  }

}
