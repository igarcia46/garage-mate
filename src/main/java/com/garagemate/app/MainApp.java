package com.garagemate.app;

import com.garagemate.model.*;
import com.garagemate.persistence.TextFileRepository;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.UUID;

public class MainApp extends Application {

    // our "db'
    private static final String DATA_FILE = "garage-data.txt";

    private TextFileRepository repo;
    private Garage garage;

    // root layout so we can swap center/bottom content
    private BorderPane root;

    // home view state
    private final ObservableList<VehicleBase> vehicleItems = FXCollections.observableArrayList();
    private ListView<VehicleBase> vehicleListView;
    private Label countLabel;

    // 'Add Vehicle' form state
    private ComboBox<String> typeBox;
    private TextField nicknameField;
    private TextField makeField;
    private TextField modelField;
    private TextField yearField;
    private TextField mileageField;
    private TextField doorsField;
    private TextField ccField;
    private Label extraLabel;

    @Override
    public void start(Stage stage) {
        repo = new TextFileRepository(DATA_FILE);
        garage = repo.loadGarage();

        root = new BorderPane();
        root.setTop(buildHeader());

        showHomeView(); // default view

        Scene scene = new Scene(root, 900, 550);
        stage.setTitle("Garage Mate");
        stage.setScene(scene);

        // save on close
        stage.setOnCloseRequest(e -> safeSave());

        stage.show();

        refreshVehicleList();
    }

    // -----------------------------
    // Header
    // -----------------------------
    private VBox buildHeader() {
        Label title = new Label("Garage Mate");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitle = new Label("Your garage vehicles (loaded from " + DATA_FILE + ")");
        subtitle.setStyle("-fx-text-fill: #555;");

        VBox header = new VBox(4, title, subtitle);
        header.setPadding(new Insets(12));
        return header;
    }

    // -----------------------------
    // Home View (list + buttons)
    // -----------------------------
    private void showHomeView() {
        // center: vehicle list
        vehicleListView = new ListView<>(vehicleItems);
        vehicleListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(VehicleBase item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNickname()
                            + "  •  " + item.getYear() + " " + item.getMake() + " " + item.getModel()
                            + "  •  " + item.getVehicleType()
                            + "  •  " + item.getCurrentMileage() + " mi");
                }
            }
        });

        root.setCenter(vehicleListView);

        // bottom: home buttons
        Button addBtn = new Button("Add Vehicle");
        Button removeBtn = new Button("Remove Selected");
        Button saveBtn = new Button("Save Now");

        // EVENTS
        addBtn.setOnAction(e -> showAddVehicleView());          // swap to 'add vehicle' form
        removeBtn.setOnAction(e -> onRemoveSelectedVehicle());  // remove
        saveBtn.setOnAction(e -> safeSave());                   // save

        HBox buttons = new HBox(10, addBtn, removeBtn, saveBtn);
        buttons.setPadding(new Insets(12));
        buttons.setAlignment(Pos.CENTER_LEFT);

        countLabel = new Label("Vehicles: " + vehicleItems.size());
        countLabel.setStyle("-fx-text-fill: #555;");

        VBox bottom = new VBox(8, buttons, countLabel);
        bottom.setPadding(new Insets(0, 12, 12, 12));
        root.setBottom(bottom);
    }

    private void onRemoveSelectedVehicle() {
        VehicleBase selected = vehicleListView.getSelectionModel().getSelectedItem();

        // check if user has selected a vehicle
        if (selected == null) {
            showError("Nothing Selected", "Please select a vehicle to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Remove");
        confirm.setHeaderText("Remove vehicle?");
        confirm.setContentText(selected.getNickname() + " (" + selected.getVehicleType() + ")");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            garage.removeVehicle(selected.getVehicleId());
            refreshVehicleList();
            safeSave();
        }
    }

    // -----------------------------
    // add vehicle view
    // -----------------------------
    private void showAddVehicleView() {
        // build form fields each time
        typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Car", "Motorcycle");
        typeBox.getSelectionModel().selectFirst();

        nicknameField = new TextField();
        makeField = new TextField();
        modelField = new TextField();
        yearField = new TextField();
        mileageField = new TextField();

        doorsField = new TextField();
        doorsField.setPromptText("e.g., 4");

        ccField = new TextField();
        ccField.setPromptText("e.g., 1103");

        extraLabel = new Label("Doors:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));

        int r = 0;
        grid.add(new Label("Type:"), 0, r);
        grid.add(typeBox, 1, r++);

        grid.add(new Label("Nickname:"), 0, r);
        grid.add(nicknameField, 1, r++);

        grid.add(new Label("Make:"), 0, r);
        grid.add(makeField, 1, r++);

        grid.add(new Label("Model:"), 0, r);
        grid.add(modelField, 1, r++);

        grid.add(new Label("Year:"), 0, r);
        grid.add(yearField, 1, r++);

        grid.add(new Label("Current Mileage:"), 0, r);
        grid.add(mileageField, 1, r++);

        grid.add(extraLabel, 0, r);
        grid.add(doorsField, 1, r);

        // switch extra field based on type
        final int extraRowIndex = r;
        typeBox.setOnAction(e -> updateExtraField(grid, extraRowIndex));

        // center becomes the form
        root.setCenter(grid);

        // bottom buttons become 'Add'/'Cancel'
        Button add = new Button("Add");
        Button cancel = new Button("Cancel");

        // EVENTS
        add.setOnAction(e -> onSubmitAddVehicle());     // create vehicle
        cancel.setOnAction(e -> showHomeView());        // cancel and go back

        HBox buttons = new HBox(10, add, cancel);
        buttons.setPadding(new Insets(12));
        buttons.setAlignment(Pos.CENTER_LEFT);

        // cool hint 'feature'
        Label hint = new Label("Tip: IDs are auto-generated (UUID).");
        hint.setStyle("-fx-text-fill: #555;");

        VBox bottom = new VBox(8, buttons, hint);
        bottom.setPadding(new Insets(0, 12, 12, 12));
        root.setBottom(bottom);
    }

    // create extra field depending on vehicle type
    private void updateExtraField(GridPane grid, int rowIndex) {
        String t = typeBox.getValue();
        if ("Car".equals(t)) {
            extraLabel.setText("Doors:");
            if (grid.getChildren().contains(ccField)) grid.getChildren().remove(ccField);
            if (!grid.getChildren().contains(doorsField)) grid.add(doorsField, 1, rowIndex);
        } else {
            extraLabel.setText("Engine CC:");
            if (grid.getChildren().contains(doorsField)) grid.getChildren().remove(doorsField);
            if (!grid.getChildren().contains(ccField)) grid.add(ccField, 1, rowIndex);
        }
    }

    private void onSubmitAddVehicle() {
        try {
            VehicleBase v = buildVehicleFromForm();
            garage.addVehicle(v);
            safeSave();
            showHomeView();
            refreshVehicleList();
        } catch (Exception ex) {
            showError("Invalid Input", ex.getMessage());
        }
    }

    private VehicleBase buildVehicleFromForm() {
        // basic validation for now
        String type = typeBox.getValue();

        // prevent file-breaking inputs since im using pipes as delimiter for my parsing
        validateNoPipes(nicknameField.getText(), "Nickname");
        validateNoPipes(makeField.getText(), "Make");
        validateNoPipes(modelField.getText(), "Model");

        int year = Integer.parseInt(yearField.getText().trim());
        int mileage = Integer.parseInt(mileageField.getText().trim());

        String id = UUID.randomUUID().toString();

        if ("Car".equals(type)) {
            int doors = Integer.parseInt(doorsField.getText().trim());
            return new Car(id, nicknameField.getText(), makeField.getText(), modelField.getText(), year, mileage, doors);
        } else {
            int cc = Integer.parseInt(ccField.getText().trim());
            return new Motorcycle(id, nicknameField.getText(), makeField.getText(), modelField.getText(), year, mileage, cc);
        }
    }

    // none of the user-input fields can contain pipes
    // except for maybe notes
    private void validateNoPipes(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        if (value.contains("|")) {
            throw new IllegalArgumentException(fieldName + " cannot contain the '|' character.");
        }
    }

    // -----------------------------
    // shared helpers
    // -----------------------------
    private void refreshVehicleList() {
        vehicleItems.setAll(garage.getAllVehicles());
        if (countLabel != null) {
            countLabel.setText("Vehicles: " + vehicleItems.size());
        }
    }

    private void safeSave() {
        try {
            repo.saveGarage(garage);
        } catch (Exception ex) {
            showError("Save Failed", ex.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
