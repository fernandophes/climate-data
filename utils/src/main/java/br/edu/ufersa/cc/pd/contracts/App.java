package br.edu.ufersa.cc.pd.contracts;

import java.io.Closeable;
import java.net.InetSocketAddress;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class App implements Runnable, Closeable {

    private InetSocketAddress address;
    private int port;

    public abstract boolean isRunning();

    public abstract String getDescription();

}
