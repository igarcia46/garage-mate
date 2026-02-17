package com.garagemate.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // Placeholder UI (will replace this with your real screens)
        StackPane root = new StackPane(new Label("Garage Mate - JavaFX is running ✅"));
        Scene scene = new Scene(root, 800, 500);

        stage.setTitle("Garage Mate");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
