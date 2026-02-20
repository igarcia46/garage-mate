package com.garagemate.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class UiUtils {
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // none of the user-input fields can contain pipes
    // except for maybe notes
    public static void validateNoPipes(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        if (value.contains("|")) {
            throw new IllegalArgumentException(fieldName + " cannot contain the '|' character.");
        }
    }

    public static Label createLabel(String text){
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: white;");
        return l;
    }

    public static Label createLabel(String text, boolean black){
        if (black) {
            Label l = new Label(text);
            l.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: black;");
            return l;
        } else return createLabel(text);
    }
}
