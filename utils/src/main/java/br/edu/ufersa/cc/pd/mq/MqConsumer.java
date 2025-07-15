package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.contracts.IMqConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MqConsumer<T> implements IMqConsumer<T> {
    // Talvez precise adicionar o nome da fila por aqui
    private final Channel channel;
    private final String routing_key;
    private final String data_model;

    public MqConsumer(Channel channel, String routing_key, String data_model) {
        this.channel = channel;
        this.routing_key = routing_key;
        this.data_model = data_model;
    }

    @Override
    public String receive() {
        try {
            DeliverCallback deliverCallback = (consumerTag, entrega) -> {
                new String(entrega.getBody(), data_model);
            };
            return channel.basicConsume(routing_key, true, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException, TimeoutException {
        this.channel.close();
    }
}
