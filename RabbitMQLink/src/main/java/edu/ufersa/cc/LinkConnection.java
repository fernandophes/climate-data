package edu.ufersa.cc;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.mq.RabbitMqConnection;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;

public class LinkConnection extends RabbitMqConnection<DroneMessage> {

  public LinkConnection(final MqConnectionData data, final Class<DroneMessage> messageType, final String queue,
                        final String exchange, final String exchangeType, final String routingKey, final String dataModel) {
    super(data, messageType, queue, exchange, exchangeType, routingKey, dataModel);
  }

  public LinkConnection(final String queue, final String exchange, final String exchangeType,
      final String routingKey, final String dataModel) {
    this(MqConnectionData.rabbitMq(), DroneMessage.class, queue, exchange, exchangeType, routingKey, dataModel);
  }

}
