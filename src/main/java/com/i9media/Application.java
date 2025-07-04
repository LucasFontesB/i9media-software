package com.i9media;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	Locale.setDefault(Locale.of("pt", "BR"));
        
        System.out.println("Locale atual: " + Locale.getDefault());
        SpringApplication.run(Application.class, args);
    }
}
