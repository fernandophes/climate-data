package br.edu.ufersa.cc.pd.contracts;

public interface MqConsumer<T> {

    T receive();

}
