package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;

public class DroneConnection extends RabbitMqConnection<DroneMessage> {

    public DroneConnection(final MqConnectionData data, final String queue, final String exchange,
            final String exchangeType, final String routingKey, final String dataModel) {
        super(data, DroneMessage.class, queue, exchange, exchangeType, routingKey, dataModel);
    }

    public DroneConnection(final String queue, final String exchange, final String exchangeType,
            final String routingKey, final String dataModel) {
        this(MqConnectionData.rabbitMq(), queue, exchange, exchangeType, routingKey, dataModel);
    }
}
