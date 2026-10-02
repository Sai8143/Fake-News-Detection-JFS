package com.truthlens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TruthLensApplication {

    public static void main(String[] args) {
        SpringApplication.run(TruthLensApplication.class, args);
        System.out.println("==================================================");
        System.out.println("🛡️ TruthLens Spring Boot JFS Backend Started!");
        System.out.println("🌐 Web Dashboard: http://localhost:8080");
        System.out.println("💾 H2 Console:    http://localhost:8080/h2-console");
        System.out.println("⚡ REST API:       http://localhost:8080/api/health");
        System.out.println("==================================================");
    }
}
