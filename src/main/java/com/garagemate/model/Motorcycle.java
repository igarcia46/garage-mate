package com.garagemate.model;

/**
 * Motorcycle is a type of VehicleBase
 */
public class Motorcycle extends VehicleBase {

    private int engineCC;

    public Motorcycle(String vehicleId,
                      String nickname,
                      String make,
                      String model,
                      int year,
                      int currentMileage,
                      int engineCC) {
        super(vehicleId, nickname, make, model, year, currentMileage);
        setEngineCC(engineCC);
    }

    public int getEngineCC() {
        return engineCC;
    }

    public void setEngineCC(int cc) {
        if (cc < 50 || cc > 3000) {
            throw new IllegalArgumentException("engineCC must be between 50 and 3000");
        }
        this.engineCC = cc;
    }

    @Override
    public String getVehicleType() {
        return "Motorcycle";
    }
}
