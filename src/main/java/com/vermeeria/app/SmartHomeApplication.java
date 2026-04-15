package com.vermeeria.app;

import com.vermeeria.persistence.JsonSmartHomeRepository;
import com.vermeeria.persistence.SmartHomeRepository;
import com.vermeeria.service.SmartHomeService;
import com.vermeeria.ui.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Entry point of the HomeFlow desktop application.
 *
 * @author Jette
 */
public class SmartHomeApplication extends Application {

    private static final String APP_NAME = "HomeFlow";

    /**
     * Starts the JavaFX application and initializes the main window.
     *
     * @param stage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        SmartHomeRepository repository =
                new JsonSmartHomeRepository(Path.of("data", "app-data.json"));

        SmartHomeService smartHomeService = new SmartHomeService(repository);
        MainController mainController = new MainController(smartHomeService);

        Scene scene = new Scene(mainController.getView(), 1280, 820);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm()
        );

        stage.setTitle(APP_NAME);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments
     */
    static void main(String[] args) {
        launch(args);
    }
}
