package com.garagemate.model;

/**
 * Car is a type of VehicleBase
 */
public class Car extends VehicleBase {

    private int numberOfDoors;

    public Car(String vehicleId,
               String nickname,
               String make,
               String model,
               int year,
               int currentMileage,
               int numberOfDoors) {
        super(vehicleId, nickname, make, model, year, currentMileage);
        setNumberOfDoors(numberOfDoors);
    }

    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(int doors) {
        if (doors < 1 || doors > 6) {
            throw new IllegalArgumentException("numberOfDoors must be between 1 and 6");
        }
        this.numberOfDoors = doors;
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }
}
