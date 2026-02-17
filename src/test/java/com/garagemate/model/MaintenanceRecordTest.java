package com.garagemate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceRecordTest {

    @Test
    void createsValidRecord() {
        MaintenanceRecord r = new MaintenanceRecord("r1", "2026-02-17", "Oil Change", 12000, "Motul");
        assertEquals("r1", r.getRecordId());
        assertEquals("Oil Change", r.getServiceType());
        assertEquals(12000, r.getMileageAtService());
    }

    @Test
    void rejectsBlankServiceType() {
        assertThrows(IllegalArgumentException.class,
                () -> new MaintenanceRecord("r1", "2026-02-17", "   ", 100, "note"));
    }

    @Test
    void rejectsNegativeMileage() {
        assertThrows(IllegalArgumentException.class,
                () -> new MaintenanceRecord("r1", "2026-02-17", "Oil", -1, ""));
    }
}
