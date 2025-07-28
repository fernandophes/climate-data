package br.edu.ufersa.cc.pdclient.controllers;

import java.io.IOException;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pdclient.App;
import br.edu.ufersa.cc.pdclient.dto.CaptureDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RealTimeController {

    private static final Logger LOG = LoggerFactory.getLogger(RealTimeController.class.getSimpleName());

    private final ObservableList<CaptureDto> allCaptures = FXCollections.observableArrayList();

    @FXML
    private Label mqLabel;

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
        mqLabel.setText(App.getMqImplementation());

        App.getReceiverService().getSubscriber()
                .subscribe(capture -> {
                    allCaptures.stream()
                            .filter(item -> item.getRegion().equals(capture.getDroneName()))
                            .findFirst()
                            .ifPresent(item -> {
                                LOG.info("Atualizando {}...", capture.getDroneName());
                                allCaptures.remove(item);
                            });
                    allCaptures.add(
                            CaptureDto.from(capture.getDroneName(), capture.getMessage(), capture.getDataFormat()));

                    allCaptures.sort((a, b) -> a.getRegion().compareTo(b.getRegion()));
                });
    }

}
