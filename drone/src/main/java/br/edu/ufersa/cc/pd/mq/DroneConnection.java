package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.utils.dto.Snapshot;

public class DroneConnection extends RabbitMqConnection<Snapshot> {

    public DroneConnection(MqConnectionData data, Class<Snapshot> messageType, String exchange, String exchangeType,
            String routingKey, String dataModel) {
        super(data, messageType, exchange, exchangeType, routingKey, dataModel);
    }

    public DroneConnection(String exchange, String exchangeType, String routingKey, String dataModel) {
        this(new MqConnectionData(), Snapshot.class, exchange, exchangeType, routingKey, dataModel);
    }

}
