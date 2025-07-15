package br.edu.ufersa.cc.pd.gateway;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import br.edu.ufersa.cc.pd.contracts.MqConsumer;
import br.edu.ufersa.cc.pd.entities.Capture;
import br.edu.ufersa.cc.pd.services.CaptureService;
import br.edu.ufersa.cc.pd.utils.contracts.App;
import br.edu.ufersa.cc.pd.utils.dto.DataFormat;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import br.edu.ufersa.cc.pd.utils.dto.Snapshot;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class Gateway extends App {

    private static final Gson GSON = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(Gateway.class.getSimpleName());

    private final MqConsumer<String> consumer;
    private final CaptureService captureService = new CaptureService();

    @Getter
    private boolean running = false;

    public Gateway(final int port, final MqConsumer<String> consumer) {
        super(null, port);
        this.consumer = consumer;
    }

    private void saveDataInDatabase(final DroneMessage message) {
        final var region = message.getDroneName();
        final var format = message.getDataFormat();
        final var formatted = message.getMessage();
        final var snapshot = Snapshot.from(formatted, format);

        final var dbFormat = new DataFormat(" | ", "[", "]");

        final var capture = new Capture();
        capture.setWeatherData(snapshot.format(dbFormat));
        capture.setRegion(region);

        captureService.create(capture);
    }

    @Override
    public void run() {
        LOG.info("Running Gateway");
        running = true;
        while (running) {
            LOG.info("Running Gateway LOOP");
            final var json = consumer.receive();
            final var message = GSON.fromJson(json, DroneMessage.class);
            saveDataInDatabase(message);
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
