package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;

public class DroneConnection extends RabbitMqConnection<String> {

    public DroneConnection(final MqConnectionData data, final String exchange, final String exchangeType,
            final String routingKey, final String dataModel) {
        super(data, String.class, exchange, exchangeType, routingKey, dataModel, dataModel);
    }

    public DroneConnection(final String exchange, final String exchangeType, final String routingKey,
            final String dataModel) {
        this(new MqConnectionData(), exchange, exchangeType, routingKey, dataModel);
    }

}
