package br.edu.ufersa.cc.pd;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.mq.RabbitMqConnection;

public class GatewayConnection extends RabbitMqConnection<String> {

    public GatewayConnection(final MqConnectionData data, final Class<String> messageType, final String exchange,
            final String exchangeType, final String routingKey, final String dataModel) {
        super(data, messageType, exchange, exchangeType, routingKey, dataModel);
    }

    public GatewayConnection(final String exchange, final String exchangeType, final String routingKey,
            final String dataModel) {
        this(new MqConnectionData(), String.class, exchange, exchangeType, routingKey, dataModel);
    }

}
