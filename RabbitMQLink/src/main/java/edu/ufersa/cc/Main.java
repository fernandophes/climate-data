package edu.ufersa.cc;

import edu.ufersa.cc.api.controllers.GetClimateDataController;
import edu.ufersa.cc.producer.OnDemandProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Main extends SpringBootServletInitializer {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        final var mqConsumerFinalClient = new LinkConnection("on_demand.all", "client_http", "fanout", "",
                "UTF-8");
        mqConsumerFinalClient.createConnection();

        final var mqProducerFClientFinalQueue = new LinkConnection("client.all", "client_final", "fanout", "",
                "UTF-8");
        mqProducerFClientFinalQueue.createConnection();

        // final var port = Integer.parseInt(System.getenv("RABBITMQ_LINK_PORT"));
        final var port = 7654;
        final var onDemandProducer = new OnDemandProducer(port, mqConsumerFinalClient, mqProducerFClientFinalQueue);
        EXECUTOR.submit(onDemandProducer);

        SpringApplication.run(GetClimateDataController.class, args);
    }
}
