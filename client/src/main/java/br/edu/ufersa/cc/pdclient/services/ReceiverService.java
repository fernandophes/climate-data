package br.edu.ufersa.cc.pdclient.services;

import java.io.IOException;
import java.util.function.Consumer;

import br.edu.ufersa.cc.pd.contracts.App;
import br.edu.ufersa.cc.pd.contracts.MqSubscriber;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import br.edu.ufersa.cc.pdclient.dto.CaptureDto;
import br.edu.ufersa.cc.pdclient.entities.Capture;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReceiverService extends App {

    private final MqSubscriber<DroneMessage> subscriber;
    private final CaptureService captureService = new CaptureService();

    private boolean running;

    public ReceiverService(final MqSubscriber<DroneMessage> consumer) {
        super(null, 0);
        this.subscriber = consumer;
    }

    @Override
    public void run() {
        running = true;

        subscriber.subscribe(message -> {
            final var capture = new Capture()
                    .setRegion(message.getDroneName())
                    .setWeatherData(message.getMessage());

            captureService.create(capture);
        });
    }

    public void subscribe(final Consumer<CaptureDto> consumer) {
        subscriber.subscribe(message -> {
            final var capture = CaptureDto.from(message.getDroneName(), message.getMessage(), message.getDataFormat());
            consumer.accept(capture);
        });
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
