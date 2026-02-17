package com.garagemate.persistence;

import com.garagemate.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TextFileRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void savesAndLoadsGarage() {
        Path file = tempDir.resolve("garage-data.txt");
        TextFileRepository repo = new TextFileRepository(file.toString());

        Garage g = new Garage();
        Car car = new Car("v1", "Daily", "Honda", "Civic", 2018, 50000, 4);
        car.addMaintenanceRecord(new MaintenanceRecord("r1", "2026-02-17", "Oil Change", 49900, "notes"));
        g.addVehicle(car);

        repo.saveGarage(g);

        Garage loaded = repo.loadGarage();
        assertEquals(1, loaded.size());

        VehicleBase v = loaded.getVehicleById("v1");
        assertNotNull(v);
        assertEquals("Car", v.getVehicleType());
        assertEquals(1, v.getMaintenanceHistory().size());
        assertEquals("Oil Change", v.getMaintenanceHistory().get(0).getServiceType());
    }
}
