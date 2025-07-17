package br.edu.ufersa.cc.pdclient.controllers;

import java.io.IOException;

import br.edu.ufersa.cc.pdclient.dto.CaptureDto;
import br.edu.ufersa.cc.pdclient.services.CaptureService;
import br.edu.ufersa.cc.pdclient.services.ReceiverService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DashboardController {

    private final ReceiverService mqService;
    private final CaptureService databaseService = new CaptureService();
    private ObservableList<CaptureDto> captures;

    @FXML
    private TableView<CaptureDto> table;

    @FXML
    private TableColumn<CaptureDto, String> regionColumn;

    @FXML
    private TableColumn<CaptureDto, Double> pressureColumn;

    @FXML
    private TableColumn<CaptureDto, Double> radiationColumn;

    @FXML
    private TableColumn<CaptureDto, Double> temperatureColumn;

    @FXML
    private TableColumn<CaptureDto, Double> humidityColumn;

    @FXML
    private void initialize() throws IOException {
        refreshTable();
        mqService.subscribe(captures::add);
    }

    @FXML
    private void refreshTable() {
        table.setRowFactory(tab -> new TableRow<CaptureDto>());

        regionColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, String>("region"));
        pressureColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, Double>("pressure"));
        radiationColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, Double>("radiation"));
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, Double>("temperature"));
        humidityColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, Double>("humidity"));

        captures = FXCollections.observableArrayList(databaseService.listAll());
        table.setItems(captures);
    }

}
