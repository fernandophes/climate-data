package br.edu.ufersa.cc.pd.controllers;

import java.io.IOException;

import br.edu.ufersa.cc.pd.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class StartController {

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
        
        App.setRoot("dashboard");
    }

    @FXML
    private void runMqttClient() throws IOException {
        App.setRoot("dashboard");
    }

}
