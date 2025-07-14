package br.edu.ufersa.cc.pd.mq;

import br.edu.ufersa.cc.pd.contracts.IMqProducer;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MqProducer<T> implements IMqProducer<T> {

    private final Channel channel;
    private final String routing_key;
    private final String exchange;
    private final String data_model;

    public MqProducer(Channel channel, String routing_key, String exchange, String data_model) {
        this.channel = channel;
        this.routing_key = routing_key;
        this.exchange = exchange;
        this.data_model = data_model;
    }

    @Override
    public void send(T message) {
        try {
            byte[] messageBytes = message.toString().getBytes(this.data_model);

            this.channel.basicPublish(this.exchange, this.routing_key, null, messageBytes);
            System.out.println("Message sent: " + message);
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
            throw new RuntimeException("Failed to send message", e);
        }
    }

//    @Override
//    public void close() throws IOException, TimeoutException {
//        this.channel.close();
//    }

}
