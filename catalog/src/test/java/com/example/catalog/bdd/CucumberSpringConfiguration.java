package com.example.catalog.bdd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.spring.CucumberContextConfiguration;

//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.testcontainers.MongoDBContainer;
import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;


@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

    //@AutoConfigureWireMock(port=8081)
    public class CucumberSpringConfiguration {

        @Autowired
        protected TestRestTemplate restTemplate; //Available to all step definitions
    
        //@Container
        //@ServiceConnection // Bridges dynamic container credentials straight into Spring
        static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5");

        static {
            //Start the container manually before the String context boots up for the container
            mongoDBContainer.start();
        }

        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry){
            registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        }

    }
