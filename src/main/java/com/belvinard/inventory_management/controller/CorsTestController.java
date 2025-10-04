package com.belvinard.inventory_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cors-test")
@CrossOrigin(origins = {
    "https://belvi-inventory-management-phi.vercel.app",
    "http://localhost:3000",
    "http://localhost:4200"
})
public class CorsTestController {

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> corsTest() {
        return ResponseEntity.ok(Map.of(
            "message", "CORS is working!",
            "status", "success"
        ));
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> corsPostTest(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(Map.of(
            "message", "CORS POST is working!",
            "received", data.toString(),
            "status", "success"
        ));
    }
}