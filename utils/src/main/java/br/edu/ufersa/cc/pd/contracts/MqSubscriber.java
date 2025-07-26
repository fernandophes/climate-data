package br.edu.ufersa.cc.pd.contracts;

import java.io.Closeable;
import java.util.function.Consumer;

public interface MqSubscriber<T> extends Closeable {

    void subscribe(Consumer<T> consumer);

}
