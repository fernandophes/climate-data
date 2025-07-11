package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.contracts.MqProducer;

public class MqttProducer<T> implements MqProducer<T> {

    @Override
    public void send(T message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'send'");
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

}
