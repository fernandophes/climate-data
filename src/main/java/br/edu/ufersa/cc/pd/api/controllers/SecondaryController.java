package br.edu.ufersa.cc.pd.api.controllers;

import java.io.IOException;

import br.edu.ufersa.cc.pd.App;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("connect");
    }
}