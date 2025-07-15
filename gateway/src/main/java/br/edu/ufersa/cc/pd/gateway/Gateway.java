package br.edu.ufersa.cc.pd.gateway;

import java.io.IOException;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.edu.ufersa.cc.pd.contracts.MqConsumer;
import br.edu.ufersa.cc.pd.entities.Capture;
import br.edu.ufersa.cc.pd.services.CaptureService;
import br.edu.ufersa.cc.pd.utils.contracts.App;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class Gateway extends App {

    private final MqConsumer<String> consumer;
    private final CaptureService captureService = new CaptureService();

    @Getter
    private boolean running;

    public Gateway(final int port, final MqConsumer<String> consumer) {
        super(null, port);
        this.consumer = consumer;
    }

    public void processData(String data) {
        final var message = JsonParser.parseString(data).getAsJsonObject();
        saveDataInDatabase(message);
    }

    private void saveDataInDatabase(final JsonObject data) {
        final var climateData = data.get("data").getAsString();
        final var region = data.get("drone").getAsString();

        final var originalData = List.of(climateData.replace("[", "").replace("]", "").split(", "));

        originalData.forEach(item -> {
            final var capture = new Capture();
            capture.setWeatherData(item.replaceAll("[,#;\\-]", "|"));
            capture.setRegion(region);

            captureService.create(capture);
        });
    }

    @Override
    public void run() {
        while (running) {
            final var message = consumer.receive();
        }
    }

    @Override
    public void close() throws IOException {
        running = false;
    }

    @Override
    public String getDescription() {
        return "Gateway";
    }

}
