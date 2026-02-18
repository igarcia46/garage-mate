package com.garagemate.app;

import com.garagemate.model.*;
import com.garagemate.persistence.TextFileRepository;

/**
 * the purpose of this class is for testing only
 * it is not used in any other part of the application
 */
public class BackendDriver {
    public static void main(String[] args) {
        // file path
        TextFileRepository repo = new TextFileRepository("garage-data.txt");

        // will need to load first thing in the real app
        Garage garage = new Garage();
        // hard code ids for now
        Car car = new Car("v1", "Daily", "Honda", "Civic", 2018, 50000, 4);
        car.addMaintenanceRecord(new MaintenanceRecord(
                "r1", "2026-02-17", "Oil Change", 49900, "Motul 7100"
        ));

        Motorcycle bike = new Motorcycle("v2", "Weekend", "Ducati", "Panigale V4", 2022, 12000, 1103);
        bike.addMaintenanceRecord(new MaintenanceRecord(
                "r2", "2026-02-10", "Chain lube", 11950, "Quick clean + lube"
        ));

        garage.addVehicle(car);
        garage.addVehicle(bike);

        // save to text file
        repo.saveGarage(garage);
        System.out.println("Saved garage. Vehicles: " + garage.size());

        // load back from text file
        Garage loaded = repo.loadGarage();
        System.out.println("Loaded garage. Vehicles: " + loaded.size());

        for (VehicleBase v : loaded.getAllVehicles()) {
            System.out.println(v);
            v.getMaintenanceHistory().forEach(r -> System.out.println("  - " + r));
        }
    }
}
