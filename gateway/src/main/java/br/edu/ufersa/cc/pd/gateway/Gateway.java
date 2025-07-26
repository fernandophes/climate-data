package br.edu.ufersa.cc.pd.gateway;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.contracts.MqProducer;
import br.edu.ufersa.cc.pd.contracts.MqSubscriber;
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

    private static final Logger LOG = LoggerFactory.getLogger(Gateway.class.getSimpleName());

    /*
     * Conexões MQ
     */
    private final MqSubscriber<DroneMessage> consumer;
    private final MqProducer<DroneMessage> onDemandProducer;
    private final MqProducer<DroneMessage> realTimeProducer;

    /*
     * Serviço do banco de dados
     */
    private final CaptureService captureService = new CaptureService();

    @Getter
    private boolean running = false;

    public Gateway(final int port, final MqSubscriber<DroneMessage> consumer,
            final MqProducer<DroneMessage> realTimeProducer, final MqProducer<DroneMessage> onDemandProducer) {
        super(null, port);
        this.consumer = consumer;
        this.realTimeProducer = realTimeProducer;
        this.onDemandProducer = onDemandProducer;
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
        running = true;

        LOG.info("Inscrevendo-se para ler a fila dos drones...", isRunning());
        consumer.subscribe(message -> {
            saveDataInDatabase(message);
            realTimeProducer.send(message);
            onDemandProducer.send(message);
        });
        LOG.info("Inscrito!", isRunning());
    }

    @Override
    public void close() throws IOException {
        running = false;

        // Close MQTT connection
        try {
            consumer.close();
            realTimeProducer.close();
            onDemandProducer.close();
        } catch (final Exception e) {
            LOG.error("Error closing MQTT connection", e);
        }
    }

    @Override
    public String getDescription() {
        return "Gateway";
    }

}
