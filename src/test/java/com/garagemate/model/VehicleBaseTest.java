package com.garagemate.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class VehicleBaseTest {

    /**
     * Test implementation subclass so we can instantiate VehicleBase
     */
    private static class TestVehicle extends VehicleBase {

        public TestVehicle(String id, String nickname, String make,
                           String model, int year, int mileage) {
            super(id, nickname, make, model, year, mileage);
        }

        @Override
        public String getVehicleType() {
            return "TestVehicle";
        }
    }

    @Test
    void constructorSetsFieldsCorrectly() {
        TestVehicle v = new TestVehicle(
                "id1", "Daily", "Test", "Vehicle", 2018, 500
        );

        assertEquals("id1", v.getVehicleId());
        assertEquals("Daily", v.getNickname());
        assertEquals("Test", v.getMake());
        assertEquals("Vehicle", v.getModel());
        assertEquals(2018, v.getYear());
        assertEquals(500, v.getCurrentMileage());
        assertEquals("TestVehicle", v.getVehicleType());
    }


    @Test
    void addMaintenanceRecordAddsRecord() {
        TestVehicle v = new TestVehicle(
                "id1", "Daily", "Honda", "Civic", 2018, 50000
        );

        MaintenanceRecord record = new MaintenanceRecord(
                "r1", "2026-02-17", "Oil Change", 49900, "Test"
        );

        v.addMaintenanceRecord(record);

        List<MaintenanceRecord> records = v.getMaintenanceHistory();

        assertEquals(1, records.size());
        assertEquals("Oil Change", records.get(0).getServiceType());
    }

    @Test
    void removeMaintenanceRecordRemovesCorrectRecord() {
        TestVehicle v = new TestVehicle(
                "id1", "Daily", "Honda", "Civic", 2018, 50000
        );

        MaintenanceRecord r1 = new MaintenanceRecord(
                "r1", "2026-02-17", "Oil", 49900, "Test"
        );
        MaintenanceRecord r2 = new MaintenanceRecord(
                "r2", "2026-02-18", "Tires", 50000, "Test"
        );

        v.addMaintenanceRecord(r1);
        v.addMaintenanceRecord(r2);

        v.removeMaintenanceRecord("r1");

        assertEquals(1, v.getMaintenanceHistory().size());
        assertEquals("r2", v.getMaintenanceHistory().get(0).getRecordId());
    }

    @Test
    void cannotAddNullMaintenanceRecord() {
        TestVehicle v = new TestVehicle(
                "id1", "Daily", "Honda", "Civic", 2018, 50000
        );

        // attaching a null Maintenance record is not allowed
        assertThrows(IllegalArgumentException.class,
                () -> v.addMaintenanceRecord(null));
    }

    @Test
    void toStringContainsImportantFields() {
        TestVehicle v = new TestVehicle(
                "id1", "Daily", "Honda", "Civic", 2018, 50000
        );

        String result = v.toString();

        assertTrue(result.contains("Daily"));
        assertTrue(result.contains("Honda"));
        assertTrue(result.contains("Civic"));
    }
}