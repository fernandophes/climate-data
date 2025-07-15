package br.edu.ufersa.cc.pd.contracts;

public interface MqProducer<T> {

    void send(T message);

}
