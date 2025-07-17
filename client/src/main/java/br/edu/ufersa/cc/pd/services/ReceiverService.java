package br.edu.ufersa.cc.pd.services;

import java.io.IOException;
import java.net.InetSocketAddress;

import br.edu.ufersa.cc.pd.contracts.App;
import br.edu.ufersa.cc.pd.contracts.MqConsumer;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReceiverService extends App {

    private final MqConsumer<DroneMessage> consumer;

    private boolean running;

    public ReceiverService(final InetSocketAddress address, final int port, final MqConsumer<DroneMessage> consumer) {
        super(address, port);
        this.consumer = consumer;
    }

    @Override
    public void run() {
        running = true;
    }

    @Override
    public void close() throws IOException {
        running = false;
    }

    @Override
    public String getDescription() {
        return "Cliente";
    }

}
