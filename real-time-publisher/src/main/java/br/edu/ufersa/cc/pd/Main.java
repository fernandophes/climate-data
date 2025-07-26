package br.edu.ufersa.cc.pd;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.contracts.MqConnection;
import br.edu.ufersa.cc.pd.mq.OnDemandConnection;
import br.edu.ufersa.cc.pd.mq.RealTimeConnection;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class.getSimpleName());
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(final String[] args) {
        LOG.info("Iniciando Publisher...");

        // Fila que recebe os dados dos drones
        final var mqConsumerFromGateway = new OnDemandConnection("climate_data.send", "drones", "fanout", "", "UTF-8");
        mqConsumerFromGateway.createConnection();

        final var mqProducer = chooseMode();
        mqProducer.createConnection();

        final var port = Integer.parseInt(System.getenv("PUBLISHER_PORT"));
        final var publisher = new Publisher(port, mqConsumerFromGateway, mqProducer);
        EXECUTOR.submit(publisher);
    }

    private static MqConnection<DroneMessage> chooseMode() {
        final var mode = Mode.valueOf(System.getenv("MODE"));

        switch (mode) {
            case REAL_TIME:
                return new RealTimeConnection("topic");

            case ON_DEMAND:
                return new OnDemandConnection("climate_data.all_real_time", "client", "fanout", "",
                        "UTF-8");

            default:
                return null;
        }
    }

}
