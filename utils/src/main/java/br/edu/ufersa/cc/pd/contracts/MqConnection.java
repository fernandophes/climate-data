package br.edu.ufersa.cc.pd.contracts;

import java.io.Closeable;

public interface MqConnection<T> extends MqConsumer<T>, MqProducer<T>, Closeable {

    void createConnection();

}
