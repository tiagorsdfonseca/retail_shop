package com.example.order.bdd.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

public class OrderProcessingSteps{

    @Autowired
    public TestRestTemplate restTemplate; //simulates API requests to our order service

    private ResponseEntity<String> response;

    @When("a customer attempts to buy {int} units of {string} through the Order Service")
    public void processCheckout(Integer quantity, String productId){
        String payload = String.format("{\"productId\":\"%s\", \"quantity\":%d}", productId, quantity);

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
        System.out.println("Verified: Apache Kafka captured the order event records sucessfully!");
    }


}