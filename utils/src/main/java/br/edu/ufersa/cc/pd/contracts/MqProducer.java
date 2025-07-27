package br.edu.ufersa.cc.pd.contracts;

import java.io.Closeable;

public interface MqProducer<T> extends Closeable {

    void send(T message);

}
