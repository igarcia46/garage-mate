package com.garagemate.ui;

import com.garagemate.model.VehicleBase;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

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

    public static void updateHomeEmptyState(ObservableList<VehicleBase> vehicleItems, Pane emptyBackgroundPane) {
        boolean empty = vehicleItems.isEmpty();

        if (emptyBackgroundPane != null) {
            emptyBackgroundPane.setVisible(empty);
            emptyBackgroundPane.setManaged(empty);
        }
    }

    public static VBox buildHeader(ObservableList<VehicleBase> vehicleItems) {
        boolean empty = vehicleItems.isEmpty();

        Label title = UiUtils.createLabel("Garage Mate");
        String message;
        if (!empty) {
            message = "Double click on a car to open it's Maintenance details.";
        } else {
            message = "Your garage is empty. Click the 'Add Vehicle' button to add a vehicle.";
        }

        Label subtitle = UiUtils.createLabel(message, true);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        VBox header = new VBox(4, subtitle);
        header.setPadding(new Insets(12));
        return header;
    }

    public static void hideHeader(BorderPane root) {
        var top = root.getTop();
        if (top != null) {
            top.setVisible(false);
            top.setManaged(false);
        }
    }

    public static void showHeader(BorderPane root) {
        var top = root.getTop();
        if (top != null) {
            top.setVisible(true);
            top.setManaged(true);
        }
    }
}
