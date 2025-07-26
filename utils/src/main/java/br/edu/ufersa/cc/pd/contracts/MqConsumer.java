package br.edu.ufersa.cc.pd.contracts;

import java.io.Closeable;

public interface MqConsumer<T> extends Closeable {

    T receive();

}
