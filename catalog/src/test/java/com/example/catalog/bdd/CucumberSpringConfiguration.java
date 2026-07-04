package com.example.catalog.bdd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.spring.CucumberContextConfiguration;


@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

    //@AutoConfigureWireMock(port=8081)
    public class CucumberSpringConfiguration {

        @Autowired
        protected TestRestTemplate restTemplate; //Available to all step definitions
    

    }
