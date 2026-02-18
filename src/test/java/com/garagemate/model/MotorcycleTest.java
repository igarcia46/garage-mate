package com.garagemate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MotorcycleTest {

    @Test
    void motorcycleTypeIsPolymorphic() {
        VehicleBase v = new Motorcycle("v1", "Daily", "Honda", "CBR 1000 RR", 2018, 500, 999);
        assertEquals("Motorcycle", v.getVehicleType());
    }

    @Test
    void rejectsInvalidEngineCc() {
        assertThrows(IllegalArgumentException.class,
                () -> new Motorcycle("v1", "Daily", "Honda", "CBR 1000 RR", 2018, 500, 0));
    }

    @Test
    void preventsRecordMileageOverCurrentMileage() {
        Motorcycle c = new Motorcycle("v1", "Daily", "Honda", "CBR 1000 RR", 2018, 500, 999);
        MaintenanceRecord r = new MaintenanceRecord("r1", "2026-02-17", "Oil Change", 600, "");
        assertThrows(IllegalArgumentException.class, () -> c.addMaintenanceRecord(r));
    }
}
