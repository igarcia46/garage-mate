package com.garagemate.app;

import com.garagemate.model.*;
import com.garagemate.persistence.TextFileRepository;
import com.garagemate.ui.UiUtils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;

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

    // details / maintenance view state
    private VehicleBase selectedVehicle;
    private final ObservableList<MaintenanceRecord> recordItems = FXCollections.observableArrayList();
    private TableView<MaintenanceRecord> recordTable;

    // add Record form state
    private TextField recordDateField;
    private TextField recordTypeField;
    private TextField recordMileageField;
    private TextArea recordNotesArea;

    // images
    private StackPane homeCenter;
    private Pane emptyBackgroundPane;
    VBox header = UiUtils.buildHeader(vehicleItems);

    // card inner class
    private static class VehicleCardCell extends ListCell<VehicleBase> {

        private final StackPane card = new StackPane();
        private final VBox textBox = new VBox(4);

        private final Label nicknameLbl = new Label();
        private final Label detailsLbl  = new Label();

        private final HBox row;

        private void applyHoverStyle() {
            card.setStyle(
                    "-fx-background-color: rgba(40,40,40,0.92);" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-color: rgba(255,255,255,0.18);" +
                            "-fx-border-radius: 14;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0.2, 0, 4);"
            );
        }

        private void applyNormalStyle() {
            card.setStyle(
                    "-fx-background-color: rgba(25,25,25,0.88);" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-color: rgba(255,255,255,0.08);" +
                            "-fx-border-radius: 14;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0.2, 0, 4);"
            );
        }

        private void applySelectedStyle() {
            card.setStyle(
                    "-fx-background-color: rgba(45,45,45,0.95);" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-color: #4FC3F7;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 14;" +
                            "-fx-effect: dropshadow(gaussian, rgba(79,195,247,0.6), 18, 0.4, 0, 0);"
            );
        }

        VehicleCardCell(BorderPane root) {
            selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    applySelectedStyle();
                } else {
                    applyNormalStyle();
                }
            });


            nicknameLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f2f2f2;");
            detailsLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #cfcfcf;");

            textBox.getChildren().addAll(nicknameLbl, detailsLbl);

            card.getChildren().add(textBox);
            card.setPadding(new Insets(12));
            card.setStyle(
                    "-fx-background-color: rgba(25, 25, 25, 0.85);" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-color: rgba(255,255,255,0.08);" +
                            "-fx-border-radius: 14;"
            );

            // 1/3 width
            card.prefWidthProperty().bind(root.widthProperty().multiply(0.33));
            card.maxWidthProperty().bind(root.widthProperty().multiply(0.33));

            // glow
            card.setOnMouseEntered(e -> {
                if (!isSelected()) applyHoverStyle();
            });

            card.setOnMouseExited(e -> {
                if (isSelected()) applySelectedStyle();
                else applyNormalStyle();
            });

            // row wrapper
            row = new HBox(card);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 0, 6, 0));

            // only allow selection if the click is inside the card
            row.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
                // mouse position relative to the card
                double x = e.getX();

                // card is left-aligned inside the row  so x > card width means click was outside the card
                if (x > card.getWidth()) {
                    // consume the event so ListView doesn't select the row
                    e.consume();

                    // clicking empty space to clearsselection
                    ListView<VehicleBase> lv = getListView();
                    if (lv != null) {
                        lv.getSelectionModel().clearSelection();
                    }
                }
            });

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setStyle("-fx-background-color: transparent;");
            applyNormalStyle();
        }

        @Override
        protected void updateItem(VehicleBase item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                return;
            }

            nicknameLbl.setText(item.getNickname());
            detailsLbl.setText(
                    item.getYear() + " " + item.getMake() + " " + item.getModel()
                            + " • " + item.getVehicleType()
                            + " • " + item.getCurrentMileage() + " mi"
            );

            setGraphic(row);
            if (isSelected()) {
                applySelectedStyle();
            } else {
                applyNormalStyle();
            }
        }
    }
    // end inner class

    @Override
    public void start(Stage stage) {
        repo = new TextFileRepository(DATA_FILE);
        garage = repo.loadGarage();

        root = new BorderPane();
        root.setTop(header);

        Scene scene = new Scene(root, 900, 550);
        stage.setTitle("Garage Mate");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setMinWidth(1200);
        stage.setMinHeight(750);
        stage.setMaxWidth(1200);
        stage.setMaxHeight(750);

        // save on close
        stage.setOnCloseRequest(e -> safeSave());

        stage.show();

        refreshVehicleList();
        showHomeView(); // start view
    }

    private void enableDeselectOnEmptyClick(ListView<?> listView) {
        listView.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {

            javafx.scene.Node n = e.getPickResult().getIntersectedNode();

            // walk up the node tree until we find a ListCell
            while (n != null && !(n instanceof ListCell)) {
                n = n.getParent();
            }

            // clicked outside any cell
            if (n == null) {
                listView.getSelectionModel().clearSelection();
                return;
            }

            // clicked an "empty" cell, theyre really blank
            @SuppressWarnings("rawtypes")
            ListCell cell = (ListCell) n;

            if (cell.isEmpty()) {
                listView.getSelectionModel().clearSelection();
            }
        });
    }

    private Pane buildHomeBackground() {
        Pane pane = new Pane();

        var url = getClass().getResource("/images/garage.jpg");
        if (url == null) {
            System.out.println("⚠ Missing image: /images/garage.jpg");
            return pane;
        }

        BackgroundSize size = new BackgroundSize(
                100, 100,
                true, true,
                false, true
        );

        BackgroundImage bg = new BackgroundImage(
                new Image(url.toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                size
        );

        pane.setBackground(new Background(bg));
        return pane;
    }

    // -----------------------------
    // Home View (list + buttons)
    // -----------------------------
    private void showHomeView() {
        UiUtils.showHeader(root);
        // vehicle list
        vehicleListView = new ListView<>(vehicleItems);
        enableDeselectOnEmptyClick(vehicleListView);

        // open details
        vehicleListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                VehicleBase v = vehicleListView.getSelectionModel().getSelectedItem();
                if (v != null) showVehicleDetailsView(v);
            }
        });

        vehicleListView.setCellFactory(list -> new VehicleCardCell(root));
        vehicleListView.setPadding(new Insets(12));
        vehicleListView.setStyle("-fx-background-color: transparent;"); // play woth this

        // empty background
        emptyBackgroundPane = buildEmptyBackgroundPane();

        Pane bg = buildHomeBackground();
        homeCenter = new StackPane(bg, vehicleListView, emptyBackgroundPane);
        root.setCenter(homeCenter);

        // bottom buttons
        Button addBtn = new Button("Add Vehicle");
        Button removeBtn = new Button("Remove Selected");
        Button saveBtn = new Button("Save Now");

        // EVENTS
        addBtn.setOnAction(e -> showAddVehicleView()); // swap to 'add vehicle' form
        removeBtn.setOnAction(e -> onRemoveSelectedVehicle()); // remove
        saveBtn.setOnAction(e -> safeSave()); //save

        HBox buttons = new HBox(10, addBtn, removeBtn, saveBtn);
        buttons.setPadding(new Insets(12));
        buttons.setAlignment(Pos.CENTER_LEFT);

        countLabel = UiUtils.createLabel("Vehicles: " + vehicleItems.size());
        countLabel.setStyle("-fx-text-fill: #555;");

        VBox bottom = new VBox(8, buttons, countLabel);
        bottom.setPadding(new Insets(0, 12, 12, 12));
        root.setBottom(bottom);

        // IMPORTANT: toggle visibility based on list contents
        UiUtils.updateHomeEmptyState(vehicleItems, emptyBackgroundPane);
    }

    // setting background when car list is empty so user sees something
    private Pane buildEmptyBackgroundPane() {
        Pane pane = new Pane();

        var url = getClass().getResource("/images/empty-garage.jpg");
        if (url == null) {
            System.out.println("⚠ Missing image: /images/empty-garage.jpg");
            return pane;
        }

        BackgroundSize size = new BackgroundSize(
                100, 100,
                true, true,
                false, true   // contain=false, cover=true -> fills the space
        );

        BackgroundImage bg = new BackgroundImage(
                new javafx.scene.image.Image(url.toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                size
        );

        pane.setBackground(new Background(bg));
        return pane;
    }

    private void onRemoveSelectedVehicle() {
        VehicleBase selected = vehicleListView.getSelectionModel().getSelectedItem();

        // check if user has selected a vehicle
        if (selected == null) {
            UiUtils.showError("Nothing Selected", "Please select a vehicle to remove.");
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
        UiUtils.hideHeader(root);
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

        extraLabel = UiUtils.createLabel("Doors:");

        GridPane grid = new GridPane();
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setMinWidth(140);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(c1, c2);
        grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
        grid.setMaxHeight(Double.MAX_VALUE);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));

        int r = 0;
        grid.add(UiUtils.createLabel("Type:"), 0, r);
        grid.add(typeBox, 1, r++);

        grid.add(UiUtils.createLabel("Nickname:"), 0, r);
        grid.add(nicknameField, 1, r++);

        grid.add(UiUtils.createLabel("Make:"), 0, r);
        grid.add(makeField, 1, r++);

        grid.add(UiUtils.createLabel("Model:"), 0, r);
        grid.add(modelField, 1, r++);

        grid.add(UiUtils.createLabel("Year:"), 0, r);
        grid.add(yearField, 1, r++);

        grid.add(UiUtils.createLabel("Current Mileage:"), 0, r);
        grid.add(mileageField, 1, r++);

        grid.add(extraLabel, 0, r);
        grid.add(doorsField, 1, r);

        for (int i = 0; i <= r; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }

        String inputStyle = """
            -fx-pref-height: 36;
            -fx-font-size: 14px;
            -fx-background-radius: 8;
        """;

        typeBox.setStyle(inputStyle);
        nicknameField.setStyle(inputStyle);
        makeField.setStyle(inputStyle);
        modelField.setStyle(inputStyle);
        yearField.setStyle(inputStyle);
        mileageField.setStyle(inputStyle);
        doorsField.setStyle(inputStyle);
        ccField.setStyle(inputStyle);


        // switch extra field based on type
        final int extraRowIndex = r;
        typeBox.setOnAction(e -> updateExtraField(grid, extraRowIndex));

        // center becomes the form
        Image bg = new Image(getClass().getResource("/images/garage.jpg").toExternalForm());

        BackgroundSize bgSize = new BackgroundSize(
                1.0, 1.0, true, true, false, true
        );
        BackgroundImage bgImg = new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize
        );

        StackPane center = new StackPane();
        center.setBackground(new Background(bgImg));

        // keep the form readable
        // grid.setStyle("-fx-background-color: rgba(255,255,255,0.85); -fx-background-radius: 10;");
        grid.setStyle("""
            -fx-background-color: rgba(30,30,30,0.75);
            -fx-background-radius: 14;
        """);
        grid.setMaxWidth(520); // keeps it from stretching too wide

        center.getChildren().add(grid);
        StackPane.setAlignment(grid, Pos.CENTER_LEFT);
        StackPane.setMargin(grid, new Insets(12));

        root.setCenter(center);

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
        Label hint = UiUtils.createLabel("Tip: IDs are auto-generated (UUID).");
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
            UiUtils.showError("Invalid Input", ex.getMessage());
        }
    }

    private VehicleBase buildVehicleFromForm() {
        // basic validation for now
        String type = typeBox.getValue();

        // prevent file-breaking inputs since im using pipes as delimiter for my parsing
        UiUtils.validateNoPipes(nicknameField.getText(), "Nickname");
        UiUtils.validateNoPipes(makeField.getText(), "Make");
        UiUtils.validateNoPipes(modelField.getText(), "Model");

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

    // add vehicle details
    private void showVehicleDetailsView(VehicleBase vehicle) {
        UiUtils.hideHeader(root);
        this.selectedVehicle = vehicle;

        // Top summary
        Label title = UiUtils.createLabel(vehicle.getNickname() + " (" + vehicle.getVehicleType() + ")");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label info = UiUtils.createLabel(
                vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel()
                        + "  •  Current Mileage: " + vehicle.getCurrentMileage()
        );
        info.setStyle("-fx-text-fill: #555;");

        VBox header = new VBox(6, title, info);
        header.setPadding(new Insets(12));

        // Maintenance table
        recordTable = new TableView<>(recordItems);

        TableColumn<MaintenanceRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getServiceDate())
        );
        dateCol.setPrefWidth(120);

        TableColumn<MaintenanceRecord, String> typeCol = new TableColumn<>("Service");
        typeCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getServiceType())
        );
        typeCol.setPrefWidth(180);

        TableColumn<MaintenanceRecord, Integer> milesCol = new TableColumn<>("Mileage");
        milesCol.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getMileageAtService())
        );
        milesCol.setPrefWidth(100);

        TableColumn<MaintenanceRecord, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getNotes())
        );
        notesCol.setPrefWidth(360);

        recordTable.getColumns().setAll(dateCol, typeCol, milesCol, notesCol);

        VBox center = new VBox(10, header, recordTable);
        center.setPadding(new Insets(0, 12, 12, 12));
        root.setCenter(center);

        // Bottom buttons
        Button addRecordBtn = new Button("Add Record");
        Button removeRecordBtn = new Button("Remove Selected");
        Button backBtn = new Button("Back");

        addRecordBtn.setOnAction(e -> showAddRecordView());
        removeRecordBtn.setOnAction(e -> onRemoveSelectedRecord());
        backBtn.setOnAction(e -> showHomeView());

        HBox buttons = new HBox(10, addRecordBtn, removeRecordBtn, backBtn);
        buttons.setPadding(new Insets(12));
        buttons.setAlignment(Pos.CENTER_LEFT);

        root.setBottom(buttons);

        refreshMaintenanceTable();
    }

    // helpers for maintenance records
    private void refreshMaintenanceTable() {
        if (selectedVehicle == null) return;
        recordItems.setAll(selectedVehicle.getMaintenanceHistory());
    }

    private void onRemoveSelectedRecord() {
        if (selectedVehicle == null) return;

        MaintenanceRecord selected = recordTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiUtils.showError("Nothing Selected", "Please select a service record to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Remove");
        confirm.setHeaderText("Remove service record?");
        confirm.setContentText(selected.getServiceType() + " on " + selected.getServiceDate());

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            selectedVehicle.removeMaintenanceRecord(selected.getRecordId());
            refreshMaintenanceTable();
            safeSave();
        }
    }

    // add record view
    private void showAddRecordView() {
        if (selectedVehicle == null) return;

        Label title = UiUtils.createLabel("Add Service Record for: " + selectedVehicle.getNickname());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // TODO: want to add validation here, currently can set anything as date which is ok for scope
        recordDateField = new TextField();
        recordDateField.setPromptText("YYYY-MM-DD (ex: 2026-02-17)");

        recordTypeField = new TextField();
        recordTypeField.setPromptText("ex: Oil Change");

        recordMileageField = new TextField();
        recordMileageField.setPromptText("ex: 52000");

        recordNotesArea = new TextArea();
        recordNotesArea.setPromptText("Optional notes...");
        recordNotesArea.setPrefRowCount(4);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));

        int r = 0;
        grid.add(title, 0, r++, 2, 1);

        grid.add(UiUtils.createLabel("Service Date:", true), 0, r);
        grid.add(recordDateField, 1, r++);

        grid.add(UiUtils.createLabel("Service Type:", true), 0, r);
        grid.add(recordTypeField, 1, r++);

        grid.add(UiUtils.createLabel("Mileage at Service:", true), 0, r);
        grid.add(recordMileageField, 1, r++);

        grid.add(UiUtils.createLabel("Notes:", true), 0, r);
        grid.add(recordNotesArea, 1, r++);

        root.setCenter(grid);

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");

        addBtn.setOnAction(e -> onSubmitAddRecord());
        cancelBtn.setOnAction(e -> showVehicleDetailsView(selectedVehicle));

        HBox buttons = new HBox(10, addBtn, cancelBtn);
        buttons.setPadding(new Insets(12));
        buttons.setAlignment(Pos.CENTER_LEFT);

        root.setBottom(buttons);
    }

    private void onSubmitAddRecord() {
        try {
            // Basic input validation
            UiUtils.validateNoPipes(recordDateField.getText(), "Service Date");
            UiUtils.validateNoPipes(recordTypeField.getText(), "Service Type");

            int miles = Integer.parseInt(recordMileageField.getText().trim());
            String notes = recordNotesArea.getText() == null ? "" : recordNotesArea.getText().trim();

            // If mileage is higher than current mileage, prompt user to update
            if (miles > selectedVehicle.getCurrentMileage()) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Update Mileage?");
                confirm.setHeaderText("Service mileage is higher than current mileage.");
                confirm.setContentText("Update vehicle mileage to " + miles + " to match this service record?");

                if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    selectedVehicle.setCurrentMileage(miles);
                } else {
                    // user cancelled update, so reject to keep data consistent
                    throw new IllegalArgumentException("Please update vehicle mileage before adding this record.");
                }
            }

            // Create record (UUID id for new records)
            MaintenanceRecord record = new MaintenanceRecord(
                    UUID.randomUUID().toString(),
                    recordDateField.getText().trim(),
                    recordTypeField.getText().trim(),
                    miles,
                    notes
            );

            selectedVehicle.addMaintenanceRecord(record);

            safeSave();
            showVehicleDetailsView(selectedVehicle);

        } catch (NumberFormatException nfe) {
            UiUtils.showError("Invalid Input", "Mileage must be a valid integer.");
        } catch (Exception ex) {
            UiUtils.showError("Invalid Input", ex.getMessage());
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
        UiUtils.updateHomeEmptyState(vehicleItems, emptyBackgroundPane);
        root.setTop(UiUtils.buildHeader(vehicleItems));
    }

    private void safeSave() {
        try {
            repo.saveGarage(garage);
        } catch (Exception ex) {
            UiUtils.showError("Save Failed", ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
