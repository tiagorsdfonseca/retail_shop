package com.example.catalog.bdd;

import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@CucumberContextConfiguration
// Spins up the application on a random port for integration testing
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

    //@AutoConfigureWireMock(port=8081)
    public class CucumberSpringConfiguration {
    

    }
