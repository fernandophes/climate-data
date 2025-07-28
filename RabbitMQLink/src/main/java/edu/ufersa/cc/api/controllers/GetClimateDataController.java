package edu.ufersa.cc.api.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import edu.ufersa.cc.LinkConnection;

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
    ResponseEntity<List<DroneMessage>> getClimateData() {
        final var climateData = Collections.synchronizedList(new ArrayList<DroneMessage>());

        mqConsumerFinalClient.getLimited(message -> climateData.add(message), 50);

        return ResponseEntity.ok(climateData);
    }
}
