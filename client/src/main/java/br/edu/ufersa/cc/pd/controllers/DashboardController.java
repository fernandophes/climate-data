package br.edu.ufersa.cc.pd.controllers;

import java.io.IOException;

import br.edu.ufersa.cc.pd.App;
import javafx.fxml.FXML;

public class DashboardController {

    @FXML
    private void switchToStart() throws IOException {
        App.setRoot("start");
    }
}