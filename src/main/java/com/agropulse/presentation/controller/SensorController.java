package com.agropulse.presentation.controller;

import com.agropulse.application.service.SensorService;
import com.agropulse.domain.model.SensorReading;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin(origins = "*")
public class SensorController {

    private final SensorService service;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String ESP32_TRIGGER_URL = "http://192.168.10.19/trigger";

    public SensorController(SensorService service) {
        this.service = service;
    }

    // ================================
    // 🔥 Trigger ESP32 and return JSON
    // ================================
    @PostMapping("/trigger")
    public ResponseEntity<Map<String, String>> triggerEsp32() {
        Map<String, String> body = new HashMap<>();

        try {
            String response = restTemplate.getForObject(ESP32_TRIGGER_URL, String.class);
            body.put("message", "Requested fresh reading from ESP32: " + response);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            body.put("error", "Failed to contact ESP32: " + e.getMessage());
            return ResponseEntity.status(500).body(body);
        }
    }
@PostMapping("/add")
public ResponseEntity<SensorReading> addReading(@RequestBody SensorReading reading) {
    SensorReading saved = service.saveReading(reading);
    return ResponseEntity.status(201).body(saved);
}

    @GetMapping("/latest")
    public ResponseEntity<SensorReading> getLatest() {
        SensorReading latest = service.getLatestReading();
        if (latest == null) return ResponseEntity.noContent().build();

        if (latest.getSoil() == null &&
            latest.getHumidity() == null &&
            latest.getTemperature() == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(latest);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SensorReading>> getAll() {
        return ResponseEntity.ok(service.getAllReadings());
    }
}
