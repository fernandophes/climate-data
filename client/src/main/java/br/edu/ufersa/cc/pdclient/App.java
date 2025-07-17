package br.edu.ufersa.cc.pdclient;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.utils.dto.DataFormat;
import br.edu.ufersa.cc.pdclient.services.CaptureService;
import br.edu.ufersa.cc.pdclient.services.ReceiverService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * JavaFX App
 */
public class App extends Application {

    public static final DataFormat FORMAT = new DataFormat(" | ", "[", "]");
    private static final Logger LOG = LoggerFactory.getLogger(App.class.getSimpleName());

    @Getter
    @Setter
    private static ReceiverService receiverService;

    @Getter
    @Setter
    private static String mqImplementation = "fila MQ";

    private static Scene scene;

    @Override
    public void start(final Stage stage) throws IOException {
        scene = new Scene(loadFXML("start"), 640, 480);
        stage.setScene(scene);
        stage.show();

        var count = new CaptureService().countAll();
        System.out.println(count);
    }

    public static void setRoot(final String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(final String fxml) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(final String[] args) {
        launch();
    }

}