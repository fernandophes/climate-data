package br.edu.ufersa.cc.pd.contracts;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public interface IMqConsumer<T> {
    String receive(Consumer<String> processData);

    void close() throws IOException, TimeoutException;
}
