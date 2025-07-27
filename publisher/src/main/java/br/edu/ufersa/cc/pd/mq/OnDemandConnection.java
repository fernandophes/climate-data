package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;

public class OnDemandConnection extends RabbitMqConnection<DroneMessage> {

    public OnDemandConnection(final MqConnectionData data, final Class<DroneMessage> messageType, final String queue,
            final String exchange, final String routingKey, final String dataModel) {
        super(data, messageType, queue, exchange, routingKey, dataModel);
    }

    public OnDemandConnection(final String queue, final String exchange, final String routingKey,
            final String dataModel) {
        this(MqConnectionData.rabbitMq(), DroneMessage.class, queue, exchange, routingKey, dataModel);
    }

}
