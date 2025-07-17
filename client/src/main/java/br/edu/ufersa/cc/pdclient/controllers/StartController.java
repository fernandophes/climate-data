package br.edu.ufersa.cc.pdclient.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.mq.MqttConnection;
import br.edu.ufersa.cc.pd.mq.RabbitMqConnection;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import br.edu.ufersa.cc.pdclient.App;
import br.edu.ufersa.cc.pdclient.services.ReceiverService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class StartController {

    private static final Logger LOG = LoggerFactory.getLogger(StartController.class.getSimpleName());

    private static final String QUEUE = "climate_data.send";
    private static final String EXCHANGE = "drones";
    private static final String EXCHANGE_TYPE = "fanout";
    private static final String ROUTING_KEY = "";
    private static final String DATA_MODEL = "UTF-8";

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private Button rabbitMqButton;

    @FXML
    private Button mqttButton;

    @FXML
    private void runRabbitMqClient() throws IOException {
        LOG.info("Dados: {}", getConnectionData());

        final var connection = new RabbitMqConnection<>(getConnectionData(), DroneMessage.class, QUEUE, EXCHANGE,
                EXCHANGE_TYPE, ROUTING_KEY, DATA_MODEL);
        connection.createConnection();

        final var receiverService = new ReceiverService(connection);
        App.setReceiverService(receiverService);
        App.setMqImplementation("RabbitMq");
        App.setRoot("dashboard");
    }

    @FXML
    private void runMqttClient() throws IOException {
        final var connection = new MqttConnection<>(getConnectionData(),
                DroneMessage.class, QUEUE);
        final var receiverService = new ReceiverService(connection);
        App.setReceiverService(receiverService);
        App.setMqImplementation("MQTT");
        App.setRoot("dashboard");
    }

    private MqConnectionData getConnectionData() {
        final var host = hostField.getText();
        final var port = Integer.parseInt(portField.getText());
        final var username = System.getenv("MQ_USERNAME");
        final var password = System.getenv("MQ_PASSWORD");

        return new MqConnectionData(host, port, username, password);
    }

}
