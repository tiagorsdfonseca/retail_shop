package com.example.order.bdd;

import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.spring.cloud.contract.wiremock.AutoConfigureWireMock;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

@CucumberContextConfiguration
// Spins up the application on a random port for integration testing
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)// Starts a WireMock server on port 8081 to act as a fake Catalog/payment APIs
    @AutoConfigureWireMock(port=8081)
    public class CucumberSpringConfiguration {
    

    }
