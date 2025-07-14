package br.edu.ufersa.cc.pd.contracts;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IMqProducer<T> {

    /**
     * Sends a message to the message queue.
     *
     * @param message the message to be sent
     */
    void send(T message);

    /**
     * Closes the producer, releasing any resources it holds.
     */
    // void close() throws IOException, TimeoutException;
    
}
