package br.edu.ufersa.cc.pd.contracts;

public interface MqConnection<T> extends MqConsumer<T>, MqProducer<T> {

    void createConnection();

}
