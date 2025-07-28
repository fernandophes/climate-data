package br.edu.ufersa.cc.pd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.mq.OnDemandConnection;
import br.edu.ufersa.cc.pd.mq.RealTimeConnection;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class.getSimpleName());
    // private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(final String[] args) {
        LOG.info("Iniciando Publisher...");

        // Modo do publisher
        final var mode = Mode.valueOf(System.getenv("MODE"));

        // Fila que recebe os dados do gateway
        final var mqConsumerFromGateway = new OnDemandConnection(mode.getQueueName(), "publisher", "", "UTF-8");
        mqConsumerFromGateway.createConnection();

        LOG.info("Consumer ok, modo {}", mode);

        try {
            // Fila para onde os dados serão enviados
            final var mqProducer = switch (mode) {
                // Caso seja em tempo real (MQTT)
                case REAL_TIME -> new RealTimeConnection(

                        // Definir função para, a partir da mensagem, obter o tópico ao qual enviar
                        message -> "client.real_time." + message.getDroneName(),

                        // Definir função para obter o tópico do qual ler
                        () -> "client.real_time.*");

                // Caso seja sob demanda (RabbitMq)
                case ON_DEMAND -> new OnDemandConnection("client.on_demand", "client", "on_demand", "UTF-8");
            };

            LOG.info("Producer preparado: {}", mqProducer);

            mqProducer.createConnection();
            LOG.info("Producer ok");

            final var publisher = new Publisher(8100, mqConsumerFromGateway, mqProducer);
            LOG.info("Publisher ok");
            publisher.run();
        } catch (Exception e) {
            LOG.error("Erro no producer", e);
        }
    }

}
