package com.garagemate.model;

import java.util.Objects;

/**
 * Represents one maintenance event for a vehicle
 * Dates are stored as String to keep persistence simple
 */
public class MaintenanceRecord {

    private final String recordId;
    private String serviceDate;   // e.g. "2026-02-17"
    private String serviceType;   // e.g. "Oil Change"
    private int mileageAtService; // odometer at time of service
    private String notes;         // optional

    public MaintenanceRecord(String recordId,
                             String serviceDate,
                             String serviceType,
                             int mileageAtService,
                             String notes) {

        this.recordId = VehicleBase.requireNonBlank(recordId, "recordId");
        setServiceDate(serviceDate);
        setServiceType(serviceType);
        setMileageAtService(mileageAtService);
        setNotes(notes);
    }

    public String getRecordId() { return recordId; }
    public String getServiceDate() { return serviceDate; }
    public String getServiceType() { return serviceType; }
    public int getMileageAtService() { return mileageAtService; }
    public String getNotes() { return notes; }

    public void setServiceDate(String date) {
        this.serviceDate = VehicleBase.requireNonBlank(date, "serviceDate");
    }

    public void setServiceType(String type) {
        this.serviceType = VehicleBase.requireNonBlank(type, "serviceType");
    }

    public void setMileageAtService(int mileage) {
        VehicleBase.validateMileage(mileage);
        this.mileageAtService = mileage;
    }

    public void setNotes(String notes) {
        // notes can be blank, but not null
        this.notes = Objects.requireNonNullElse(notes, "").trim();
    }

    @Override
    public String toString() {
        return "MaintenanceRecord{" +
                "recordId='" + recordId + '\'' +
                ", serviceDate='" + serviceDate + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", mileageAtService=" + mileageAtService +
                ", notes='" + notes + '\'' +
                '}';
    }
}
