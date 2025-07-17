package br.edu.ufersa.cc.pd;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.mq.RabbitMqConnection;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;

public class GatewayConnection extends RabbitMqConnection2<DroneMessage> {

    public GatewayConnection(final MqConnectionData data, final Class<DroneMessage> messageType, final String queue,
            final String exchange, final String exchangeType, final String routingKey, final String dataModel) {
        super(data, messageType, queue, exchange, exchangeType, routingKey, dataModel);
    }

    public GatewayConnection(final String queue, final String exchange, final String exchangeType,
            final String routingKey, final String dataModel) {
        this(new MqConnectionData(), DroneMessage.class, queue, exchange, exchangeType, routingKey, dataModel);
    }

}
