package edu.ufersa.cc.api.controllers;

import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import edu.ufersa.cc.LinkConnection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RestController
public class GetClimateDataController {

    final LinkConnection mqConsumerFinalClient;

    public GetClimateDataController() {
        final var mqProducerFinalClient = new LinkConnection("client.on_demand", "client", "",
                "UTF-8");
        this.mqConsumerFinalClient = mqProducerFinalClient;
        mqProducerFinalClient.createConnection();
    }

    @GetMapping("/climate-data")
    ResponseEntity<?> getClimateData() {

        List<DroneMessage> climateData = Collections.synchronizedList(new ArrayList<>());

        mqConsumerFinalClient.getLimited(message -> {
            climateData.add(message);
        }, 50);

        if (climateData.isEmpty()) {
            return ResponseEntity.ok("No climate data available in RabbitMQ queue.");
        }
        return ResponseEntity.ok(climateData);
    }
}
