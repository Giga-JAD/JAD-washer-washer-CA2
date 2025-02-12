package com.Giga_JAD.Washer_Washer;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WasherWasherApplication {
    public static void main(String[] args) {
        // Load .env file
        Dotenv dotenv = Dotenv.load();
        
        // Debug print
        System.out.println("DB_URL from .env: " + dotenv.get("DB_URL"));
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        
        // Ensure the URL starts with jdbc:
        String dbUrl = dotenv.get("DB_URL");
        if (!dbUrl.startsWith("jdbc:")) {
            dbUrl = "jdbc:" + dbUrl;
        }
        
        // Set system properties
        System.setProperty("spring.datasource.url", dbUrl);
        System.setProperty("spring.datasource.username", dotenv.get("DB_USER"));
        System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));
        System.setProperty("spring.datasource.driver-class-name", dotenv.get("DB_CLASS"));
        
        SpringApplication.run(WasherWasherApplication.class, args);
    }
}