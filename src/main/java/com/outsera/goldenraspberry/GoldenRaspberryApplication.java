package com.outsera.goldenraspberry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Golden Raspberry Awards API.
 *
 * This RESTful API provides analysis of Golden Raspberry Awards
 * "Worst Film" category, specifically identifying producers with
 * maximum and minimum intervals between consecutive wins.
 *
 * @author Sato
 * @version 1.0.0
 */
@SpringBootApplication
public class GoldenRaspberryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoldenRaspberryApplication.class, args);
    }

}
