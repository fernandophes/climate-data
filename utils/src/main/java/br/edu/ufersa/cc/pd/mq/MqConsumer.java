package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.contracts.IMqConsumer;

public class MqConsumer<T> implements IMqConsumer<T> {
    @Override
    public void receive(T message) {

    }

    @Override
    public void close() {

    }
}
