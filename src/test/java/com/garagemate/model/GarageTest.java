package com.garagemate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GarageTest {

    @Test
    void addAndRemoveVehicle() {
        Garage g = new Garage();
        Car c = new Car("v1", "Daily", "Honda", "Civic", 2018, 50000, 4);

        g.addVehicle(c);
        assertEquals(1, g.size());
        assertTrue(g.containsVehicle("v1"));

        assertTrue(g.removeVehicle("v1"));
        assertEquals(0, g.size());
    }

    @Test
    void preventsDuplicateVehicleIds() {
        Garage g = new Garage();
        g.addVehicle(new Car("v1", "A", "Honda", "Civic", 2018, 1, 4));
        assertThrows(IllegalArgumentException.class,
                () -> g.addVehicle(new Car("v1", "B", "Honda", "Accord", 2019, 2, 4)));
    }
}
