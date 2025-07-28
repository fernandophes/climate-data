package br.edu.ufersa.cc.pdclient.controllers;

import java.io.IOException;

import br.edu.ufersa.cc.pd.dto.MqConnectionData;
import br.edu.ufersa.cc.pd.mq.MqttConnection;
import br.edu.ufersa.cc.pd.mq.RabbitMqConnection;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import br.edu.ufersa.cc.pdclient.App;
import br.edu.ufersa.cc.pdclient.Mode;
import br.edu.ufersa.cc.pdclient.services.ReceiverService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class StartController {

    @FXML
    private TextField hostField;

    @FXML
    private Button rabbitMqButton;

    @FXML
    private Button httpButton;

    @FXML
    private Button mqttButton;

    @FXML
    private void runRabbitMqClient() throws IOException {
        final var connection = new RabbitMqConnection<>(getConnectionData(Mode.ON_DEMAND), DroneMessage.class, "client.on_demand",
                "client", "", "UTF-8");
        connection.createConnection();

        final var receiverService = new ReceiverService(connection);
        App.setReceiverService(receiverService);
        App.setMqImplementation("RabbitMq");
        App.setRoot("dashboard");
    }

    @FXML
    private void runHttpClient() throws IOException {
        App.setHost(hostField.getText());
        App.setMqImplementation("HTTP");
        App.setRoot("on-demand");
    }

    @FXML
    private void runMqttClient() throws IOException {
        final var connection = new MqttConnection<>(getConnectionData(Mode.REAL_TIME), DroneMessage.class,
                message -> "client.real_time." + message.getDroneName(), () -> "client.real_time.*");
        connection.createConnection();
        final var receiverService = new ReceiverService(connection);
        App.setReceiverService(receiverService);
        App.setMqImplementation("MQTT");
        App.setRoot("real-time");
    }

    private MqConnectionData getConnectionData(final Mode mode) {
        final var host = hostField.getText();
        final var port = Integer.parseInt(System.getenv(mode.getPortEnv()));
        final var username = System.getenv("MQ_USERNAME");
        final var password = System.getenv("MQ_PASSWORD");
        
        return new MqConnectionData(host, port, username, password);
    }

}
