package com.garagemate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    void carTypeIsPolymorphic() {
        VehicleBase v = new Car("v1", "Daily", "Honda", "Civic", 2018, 50000, 4);
        assertEquals("Car", v.getVehicleType());
    }

    @Test
    void rejectsInvalidDoorCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Car("v1", "Daily", "Honda", "Civic", 2018, 50000, 0));
    }

    @Test
    void preventsRecordMileageOverCurrentMileage() {
        Car c = new Car("v1", "Daily", "Honda", "Civic", 2018, 50000, 4);
        MaintenanceRecord r = new MaintenanceRecord("r1", "2026-02-17", "Oil Change", 60000, "");
        assertThrows(IllegalArgumentException.class, () -> c.addMaintenanceRecord(r));
    }
}
