package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;

public class DroneConnection extends RabbitMqConnection<String> {

    public DroneConnection(final MqConnectionData data, final String queue, final String exchange,
            final String exchangeType, final String routingKey, final String dataModel) {
        super(data, String.class, queue, exchange, exchangeType, routingKey, dataModel);
    }

    public DroneConnection(final String queue, final String exchange, final String exchangeType,
            final String routingKey, final String dataModel) {
        this(MqConnectionData.rabbitMq(), queue, exchange, exchangeType, routingKey, dataModel);
    }

}
