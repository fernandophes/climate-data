package br.edu.ufersa.cc.pd.contracts;

public interface MqConnection<T> extends MqSubscriber<T>, MqProducer<T> {

    void createConnection();

}
