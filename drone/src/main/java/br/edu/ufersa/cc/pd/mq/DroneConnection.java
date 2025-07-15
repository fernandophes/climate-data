package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.utils.dto.Snapshot;

public class DroneConnection extends RabbitMqConnection<Snapshot> {

    public DroneConnection(final MqConnectionData data, final String exchange, final String exchangeType,
            final String routingKey, final String dataModel) {
        super(data, Snapshot.class, exchange, exchangeType, routingKey, dataModel);
    }

    public DroneConnection(final String exchange, final String exchangeType, final String routingKey,
            final String dataModel) {
        this(new MqConnectionData(), exchange, exchangeType, routingKey, dataModel);
    }

}
