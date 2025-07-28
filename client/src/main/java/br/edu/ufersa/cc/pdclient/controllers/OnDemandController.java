package br.edu.ufersa.cc.pdclient.controllers;

import java.io.IOException;

import br.edu.ufersa.cc.pdclient.App;
import br.edu.ufersa.cc.pdclient.dto.CaptureDto;
import br.edu.ufersa.cc.pdclient.services.CaptureService;
import br.edu.ufersa.cc.pdclient.services.HttpService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OnDemandController {

    private static final String TODAS = "Todas";

    private final CaptureService databaseService = new CaptureService();
    private final HttpService httpService = new HttpService();

    private final ObservableList<CaptureDto> allCaptures = FXCollections.observableArrayList();
    private final ObservableList<String> regions = FXCollections.observableArrayList(TODAS);

    @FXML
    private Label totalCapturesLabel;

    @FXML
    private Label mqLabel;

    @FXML
    private ComboBox<String> regionsComboBox;

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
        table.setRowFactory(tab -> new TableRow<CaptureDto>());

        regionColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, String>("region"));
        pressureColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, Double>("pressure"));
        radiationColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, Double>("radiation"));
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, Double>("temperature"));
        humidityColumn.setCellValueFactory(new PropertyValueFactory<CaptureDto, Double>("humidity"));

        table.setItems(allCaptures);
        regionsComboBox.setItems(regions);
        regionsComboBox.setValue(TODAS);
        mqLabel.setText(App.getMqImplementation());
    }

    @FXML
    private void refreshTable() {
        httpService.get()
                .forEach(message -> {
                    final var capture = CaptureDto.from(message.getDroneName(), message.getMessage(),
                            message.getDataFormat());
                    databaseService.create(capture, App.FORMAT);

                    if (!regions.contains(capture.getRegion())) {
                        regions.add(capture.getRegion());
                    }
                });

        filterByRegion();
    }

    @FXML
    private void filterByRegion() {
        final var region = regionsComboBox.getValue();

        if (TODAS.equals(region)) {
            allCaptures.setAll(databaseService.listAll().reversed());
        } else {
            allCaptures.setAll(databaseService.listByRegion(region).reversed());
        }

        totalCapturesLabel.setText(String.valueOf(allCaptures.size()));
    }

}
