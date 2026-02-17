package com.garagemate.persistence;

import com.garagemate.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads Garage Mate data to a local TEXT FILE.
 * No UI code here.
 */
public class TextFileRepository {

    private final Path filePath;

    public TextFileRepository(String filePath) {
        this.filePath = Paths.get(VehicleBase.requireNonBlank(filePath, "filePath"));
    }

    public Garage loadGarage() {
        Garage garage = new Garage();
        if (!Files.exists(filePath)) {
            return garage; // empty garage if file not present yet
        }

        try {
            List<String> lines = Files.readAllLines(filePath);

            // pass 1: load vehicles
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                if (line.startsWith("V|")) {
                    VehicleBase v = parseVehicleLine(line);
                    garage.addVehicle(v);
                }
            }

            // pass 2: load records and attach to vehicles
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                if (line.startsWith("R|")) {
                    ParsedRecord pr = parseRecordLine(line);
                    VehicleBase vehicle = garage.getVehicleById(pr.vehicleId);
                    if (vehicle != null) {
                        vehicle.addMaintenanceRecord(pr.record);
                    }
                }
            }

            return garage;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load garage from file: " + filePath, e);
        }
    }

    public void saveGarage(Garage garage) {
        if (garage == null) throw new IllegalArgumentException("garage cannot be null");

        List<String> lines = new ArrayList<>();

        for (VehicleBase v : garage.getAllVehicles()) {
            lines.add(toVehicleLine(v));
        }
        for (VehicleBase v : garage.getAllVehicles()) {
            for (MaintenanceRecord r : v.getMaintenanceHistory()) {
                lines.add(toRecordLine(v.getVehicleId(), r));
            }
        }

        try {
            Files.createDirectories(filePath.getParent() == null ? Paths.get(".") : filePath.getParent());
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save garage to file: " + filePath, e);
        }
    }

    // ---- Parsing/formatting helpers ----

    private VehicleBase parseVehicleLine(String line) {
        // V|type|vehicleId|nickname|make|model|year|mileage|extra
        String[] parts = line.split("\\|", -1);
        if (parts.length < 9) throw new IllegalArgumentException("Invalid vehicle line: " + line);

        String type = parts[1];
        String vehicleId = parts[2];
        String nickname = parts[3];
        String make = parts[4];
        String model = parts[5];
        int year = Integer.parseInt(parts[6]);
        int mileage = Integer.parseInt(parts[7]);
        int extra = Integer.parseInt(parts[8]);

        return switch (type) {
            case "Car" -> new Car(vehicleId, nickname, make, model, year, mileage, extra);
            case "Motorcycle" -> new Motorcycle(vehicleId, nickname, make, model, year, mileage, extra);
            default -> throw new IllegalArgumentException("Unknown vehicle type: " + type);
        };
    }

    private ParsedRecord parseRecordLine(String line) {
        // R|vehicleId|recordId|date|serviceType|mileageAtService|notes
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) throw new IllegalArgumentException("Invalid record line: " + line);

        String vehicleId = parts[1];
        String recordId = parts[2];
        String date = parts[3];
        String serviceType = parts[4];
        int mileageAtService = Integer.parseInt(parts[5]);
        String notes = unescape(parts[6]);

        MaintenanceRecord record = new MaintenanceRecord(recordId, date, serviceType, mileageAtService, notes);
        return new ParsedRecord(vehicleId, record);
    }

    private String toVehicleLine(VehicleBase v) {
        int extra;
        if (v instanceof Car c) extra = c.getNumberOfDoors();
        else if (v instanceof Motorcycle m) extra = m.getEngineCC();
        else throw new IllegalArgumentException("Unsupported vehicle type: " + v.getClass().getName());

        return String.join("|",
                "V",
                v.getVehicleType(),
                v.getVehicleId(),
                v.getNickname(),
                v.getMake(),
                v.getModel(),
                String.valueOf(v.getYear()),
                String.valueOf(v.getCurrentMileage()),
                String.valueOf(extra)
        );
    }

    private String toRecordLine(String vehicleId, MaintenanceRecord r) {
        return String.join("|",
                "R",
                vehicleId,
                r.getRecordId(),
                r.getServiceDate(),
                r.getServiceType(),
                String.valueOf(r.getMileageAtService()),
                escape(r.getNotes())
        );
    }

    // Keep notes safe even if user types "|" in notes.
    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("|", "\\|");
    }

    private static String unescape(String value) {
        if (value == null) return "";
        // Simple unescape for our two escape rules
        return value.replace("\\|", "|").replace("\\\\", "\\");
    }

    private record ParsedRecord(String vehicleId, MaintenanceRecord record) {}
}
