package com.example.catalog.bdd.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;



public class InventorySteps {
    
    @Autowired
    public TestRestTemplate restTemplate; //simulates API requests to our order service

    private ResponseEntity<String> response;


    @When("the manager restocks {int} units of product {string}")
    public void processInventoryUpdate(Integer incomingStock, String productId){
        String payload = String.format("{\"productId\":\"%s\", \"quantity\":%d}", productId, incomingStock);

        //Triggers our actual InventoryController endpoint
        response = restTemplate.postForEntity("/catalog", payload, String.class);
    }

    @Then("the total stock for {string} should be {int}")
    public void verifySucessfullInventoryUpdate(String productId, Integer expectedStock){

        // Implementation for verifying successful inventory update
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(String.format("\"productId\":\"%s\", \"expectedStock\":%d", productId, expectedStock)));
    }

    @Then("the system should reject the update with an error {string}")
    public void verifyRejectedInventoryUpdate(String expectedErrorMessage){
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(HttpStatus.BAD_REQUEST.toString().equals(response.getStatusCode().toString()));

    }

    @And("the total stock for {string} should remain {int}")
    public void verifyUnchangedStock(String productId, Integer expectedStock){
        assertTrue(response.getBody().contains(String.format("\"productId\":\"%s\", \"expectedStock\":%d", productId, expectedStock)));
    }

   /*  @When("a customer attempts to buy {int} units of {string} through the Order Service")
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
    } */
}
