package com.example.order.bdd.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class OrderProcessingSteps{

    @Autowired
    public TestRestTemplate restTemplate; //simulates API requests to our order service

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    private ResponseEntity<String> response;

    @When("a customer attempts to buy {int} units of {string} through the Order Service")
    public void processCheckout(Integer quantity, String productId){
        //String payload = String.format("{\"productId\":\"%s\", \"quantity\":%d}", productId, quantity);
        Map<String, Object> payload = Map.of(
            "productId", productId,
            "quantity", quantity
        );
        //Triggers our actual  OrderController endpoint
        response = restTemplate.postForEntity("/orders", payload, String.class);
    }

    @Then("the Order Service should reject the transaction with the status {string}")
    public void verifyRejectedTransaction(String expectedStatus){
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(expectedStatus));
    }

    @And("the error details should contain {string}")
    public void verifyErrorDetails(String expectedErrorMessage) {
        assertTrue(response.getBody().contains(expectedErrorMessage),
            String.format("Expected error body to contain '%s' but got %s", expectedErrorMessage, response.getBody()));
    }


    @Then("the Order Service should approve the transaction with status {string}")
    public void verifySucessfulTransaction(String expectedStatus){
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains(expectedStatus));
    }

    @And("an order transaction event should be published to the streaming cluster")
    public void verifyKafkaMessageIsEmitted(){
        //System.out.println("Verified: Apache Kafka captured the order event records sucessfully!");
        Consumer<String, Object> consumer = consumerFactory.createConsumer("test-group","test-client");
        consumer.subscribe(Collections.singleton("order-transaction"));

        //Read the single record out from the topic with a 3-second safety timeout
       // ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "order-transactions", 3000);
       ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(
        consumer,
        "order-transactions",
        Duration.ofSeconds(3)
       ); 
       
       //Asertions
        assertNotNull(singleRecord, "Failed to retrieve the emitted message from the streaming cluster!");
        assertTrue(singleRecord.value().toString().contains("APPROVED"), "Kafka event state should be set to APPROVED");
    
        consumer.close();
    }


}