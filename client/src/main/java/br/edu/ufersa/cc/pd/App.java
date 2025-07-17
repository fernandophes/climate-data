package br.edu.ufersa.cc.pd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.services.CaptureService;

/**
 * JavaFX App
 */
public class App extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(App.class.getSimpleName());

    private static Scene scene;
    private static CaptureService captureService = new CaptureService();

    @Override
    public void start(final Stage stage) throws IOException {
        scene = new Scene(loadFXML("start"), 640, 480);
        stage.setScene(scene);
        stage.show();

        final var captures = captureService.listAll();
        LOG.info("Capturas encontradas: {}", captures.size());
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