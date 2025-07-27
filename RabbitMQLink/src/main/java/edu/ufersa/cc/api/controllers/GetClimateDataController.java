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
@RequestMapping("/climate-data")
public class GetClimateDataController {

    final LinkConnection mqConsumerFinalClient;

    public GetClimateDataController() {
        final var mqProducerFinalClient = new LinkConnection("on_demand.all", "client_http", "fanout", "",
                "UTF-8");
        this.mqConsumerFinalClient = mqProducerFinalClient;
        mqProducerFinalClient.createConnection();
    }

    @GetMapping
    ResponseEntity<List<DroneMessage>> getClimateData() {

        List<DroneMessage> climateData = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(100);

        mqConsumerFinalClient.subscribe(message -> {
            if (latch.getCount() > 0) {
                climateData.add(message);
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok(climateData);
    }
}
