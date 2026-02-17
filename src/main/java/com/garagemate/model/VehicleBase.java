package com.garagemate.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for all vehicles in Garage Mate.
 * This class contains shared fields and behavior for Car and Motorcycle.
 * NOTE: No JavaFX/UI code belongs here.
 */
public abstract class VehicleBase {

    private final String vehicleId; // immutable unique ID
    private String nickname;
    private String make;
    private String model;
    private int year;
    private int currentMileage;

    private final List<MaintenanceRecord> maintenanceHistory = new ArrayList<>();

    protected VehicleBase(String vehicleId,
                          String nickname,
                          String make,
                          String model,
                          int year,
                          int currentMileage) {

        this.vehicleId = requireNonBlank(vehicleId, "vehicleId");

        setNickname(nickname);
        setMake(make);
        setModel(model);
        setYear(year);
        setCurrentMileage(currentMileage);
    }

    // ---- Getters ----
    public String getVehicleId() { return vehicleId; }
    public String getNickname() { return nickname; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public int getCurrentMileage() { return currentMileage; }

    /**
     * Returns an unmodifiable view of the maintenance history.
     */
    public List<MaintenanceRecord> getMaintenanceHistory() {
        return Collections.unmodifiableList(maintenanceHistory);
    }

    // ---- Setters with validation ----
    public void setNickname(String nickname) {
        this.nickname = requireNonBlank(nickname, "nickname");
    }

    public void setMake(String make) {
        this.make = requireNonBlank(make, "make");
    }

    public void setModel(String model) {
        this.model = requireNonBlank(model, "model");
    }

    public void setYear(int year) {
        validateYear(year);
        this.year = year;
    }

    public void setCurrentMileage(int mileage) {
        validateMileage(mileage);
        this.currentMileage = mileage;
    }

    // ---- Maintenance behavior ----
    public void addMaintenanceRecord(MaintenanceRecord record) {
        Objects.requireNonNull(record, "record cannot be null");

        // sanity check: don't allow a service mileage greater than current mileage
        if (record.getMileageAtService() > this.currentMileage) {
            throw new IllegalArgumentException("mileageAtService cannot exceed currentMileage");
        }
        maintenanceHistory.add(record);
    }

    public boolean removeMaintenanceRecord(String recordId) {
        String id = requireNonBlank(recordId, "recordId");
        return maintenanceHistory.removeIf(r -> r.getRecordId().equals(id));
    }

    /**
     * Vehicle type is determined by subclass (polymorphism).
     * Ex: Car returns "Car", Motorcycle returns "Motorcycle".
     */
    public abstract String getVehicleType();

    @Override
    public String toString() {
        return getVehicleType() + "{" +
                "vehicleId='" + vehicleId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", currentMileage=" + currentMileage +
                ", maintenanceCount=" + maintenanceHistory.size() +
                '}';
    }

    // ---- Helpers ----
    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    protected static void validateMileage(int mileage) {
        if (mileage < 0) {
            throw new IllegalArgumentException("mileage must be >= 0");
        }
    }

    protected static void validateYear(int year) {
        // Simple validation: reasonable range. Adjust if your instructor wants different rules.
        if (year < 1886 || year > 2100) {
            throw new IllegalArgumentException("year must be between 1886 and 2100");
        }
    }
}
