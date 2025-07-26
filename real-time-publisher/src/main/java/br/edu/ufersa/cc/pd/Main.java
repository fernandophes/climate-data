package br.edu.ufersa.cc.pd;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.mq.OnDemandConnection;
import br.edu.ufersa.cc.pd.mq.RealTimeConnection;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class.getSimpleName());
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(final String[] args) {
        LOG.info("Iniciando Publisher...");

        // Modo do publisher
        final var mode = Mode.valueOf(System.getenv("MODE"));

        // Fila que recebe os dados do gateway
        final var mqConsumerFromGateway = new OnDemandConnection(mode.getQueueName(), "drones", "fanout", "", "UTF-8");
        mqConsumerFromGateway.createConnection();

        // Fila para onde os dados serão enviados
        final var mqProducer = switch (mode) {
            case REAL_TIME -> new RealTimeConnection("real_time");
            case ON_DEMAND -> new OnDemandConnection("climate_data.on_demand", "client", "fanout", "", "UTF-8");
        };
        mqProducer.createConnection();

        final var port = Integer.parseInt(System.getenv("PUBLISHER_PORT"));
        final var publisher = new Publisher(port, mqConsumerFromGateway, mqProducer);
        EXECUTOR.submit(publisher);
    }

}
