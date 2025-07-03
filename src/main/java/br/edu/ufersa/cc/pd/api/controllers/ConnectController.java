package br.edu.ufersa.cc.pd.api.controllers;

import java.io.IOException;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ConnectController {

    @FXML
    private TextField addressField;

    @FXML
    private TextField portField;

    @FXML
    private Button retryButton;

    @FXML
    protected void initialize() {
        // addressField.setText(LocalizationService.getHost());
        // portField.setText(LocalizationService.getPort().toString());
    }

    @FXML
    private void connect() throws IOException {
        // LocalizationService.setHost(addressField.getText());
        // LocalizationService.setPort(Integer.parseInt(portField.getText()));

        // try {
        //     LocalizationService.localizeAndUpdate();
        //     App.setRoot("listAll");
        // } catch (final IOException | CustomException e) {
        //     final var alert = new Alert(AlertType.ERROR);
        //     Throwable exception = e;

        //     while (exception != null) {
        //         alert.setContentText(exception.getMessage());

        //         if (exception instanceof OperationException) {
        //             alert.setTitle("Operação inválida");
        //             LOG.error(alert.getTitle());
        //             break;
        //         } else if (exception instanceof ConnectionException) {
        //             alert.setTitle("Conexão recusada");
        //             LOG.error(alert.getTitle());
        //             break;
        //         } else {
        //             alert.setTitle("Ocorreu um erro");
        //             LOG.error(alert.getTitle(), e);
        //         }

        //         exception = exception.getCause();
        //     }

        //     alert.setHeaderText(alert.getTitle());
        //     alert.show();
        // }
    }

    @FXML
    private void verifyRetryButton(final Event ignore) {
        retryButton.setDisable(addressField.getText().isEmpty() || portField.getText().isEmpty());
    }

}
