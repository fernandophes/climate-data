package br.edu.ufersa.cc.pd.contracts;

import java.util.function.Consumer;

public interface MqSubscriber<T> {

    String subscribe(Consumer<T> consumer);

}
