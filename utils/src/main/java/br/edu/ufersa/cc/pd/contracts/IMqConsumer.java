package br.edu.ufersa.cc.pd.contracts;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IMqConsumer<T> {
    String receive();

    void close() throws IOException, TimeoutException;
}
