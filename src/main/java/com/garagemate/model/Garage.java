package com.garagemate.model;

import java.util.*;

/**
 * Garage is a container for vehicles
 */
public class Garage {

    private final Map<String, VehicleBase> vehicles = new LinkedHashMap<>();

    public Garage() {}

    public List<VehicleBase> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    public VehicleBase getVehicleById(String vehicleId) {
        String id = VehicleBase.requireNonBlank(vehicleId, "vehicleId");
        return vehicles.get(id);
    }

    public void addVehicle(VehicleBase vehicle) {
        Objects.requireNonNull(vehicle, "vehicle cannot be null");
        if (vehicles.containsKey(vehicle.getVehicleId())) {
            throw new IllegalArgumentException("Duplicate vehicleId: " + vehicle.getVehicleId());
        }
        vehicles.put(vehicle.getVehicleId(), vehicle);
    }

    public boolean removeVehicle(String vehicleId) {
        String id = VehicleBase.requireNonBlank(vehicleId, "vehicleId");
        return vehicles.remove(id) != null;
    }

    public boolean containsVehicle(String vehicleId) {
        String id = VehicleBase.requireNonBlank(vehicleId, "vehicleId");
        return vehicles.containsKey(id);
    }

    public int size() {
        return vehicles.size();
    }

    public void clear() {
        vehicles.clear();
    }
}
