package com.example.catalog.bdd.steps;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.catalog.bdd.CucumberSpringConfiguration;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;



public class InventorySteps extends CucumberSpringConfiguration {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    public TestRestTemplate restTemplate; //simulates API requests to our order service

    private ResponseEntity<String> response;

    private static final String PRODUCT_ID = "PROD-BLUE-M";

    @Before
    public void sendDatabaseBeforeTest(){
        //Automatically clear out old test runs and inject a fresh base item
        productRepository.deleteAll();

        Product defaultProduct = new Product (PRODUCT_ID, "Classic Blue T-Shirt", 10, 19.99);
        productRepository.save(defaultProduct);
    }


    @When("the manager restocks {int} units of product {string}")
    public void processInventoryUpdate(Integer incomingStock, String productId){
       // String payload = String.format("{\"productId\":\"%s\", \"quantity\":%d}", productId, incomingStock);
        String url = "/products/" + productId + "/restock";
        Map<String,Object> requestPayload = Map.of("quantity", incomingStock);
 
        //Triggers our actual InventoryController endpoint
        //response = restTemplate.postForEntity("/catalog", payload, String.class);
        response = restTemplate.postForEntity(url,requestPayload,String.class);
    }

    @Then("the system should respond with {string}")
    public void verifyInventoryUpdateOutcome(String expectedOutcome){

        if("SUCCESS".equalsIgnoreCase(expectedOutcome)) {
            //Assertions for the happy path
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("Stock updated sucessfully"));
        }
        else if("REJECTED".equalsIgnoreCase(expectedOutcome)){
            // Assertions for the sad path / validation failure
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().contains("Invalid stock increment value"));
        }
        // Implementation for verifying successful inventory update
        
        //assertTrue(response.getBody().contains(String.format("\"productId\":\"%s\", \"expectedStock\":%d", productId, expectedStock)));

        //VERIFY: Pull the fresh record out of the database to check if the logic worked
       /*  Product updatedProduct = productRepository.findById(productId)
            .orElseThrow(() -> new AssertionError("Product not found in DB after restock!"));*/

        //assertEquals(expectedStock, updatedProduct.getStock(), "Database stock did not update correctly!");
    }

  /*   @Then("the system should reject the update with an error {string}")
    public void verifyRejectedInventoryUpdate(String expectedErrorMessage){
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(HttpStatus.BAD_REQUEST.toString().equals(response.getStatusCode().toString()));

    } */

    @And("the total stock for {string} {string} {int}")
    public void verifyStock(String productId, String expectationCondition, Integer expectedStock){
        //assertTrue(response.getBody().contains(String.format("\"productId\":\"%s\", \"expectedStock\":%d", productId, expectedStock)));
       // Product updatedProduct = productRepository.findById(productId)
        //    .orElseThrow(() -> new AssertionError("Product not found in DB after restock!"));
          Product currentProduct = productRepository.findById(productId)
            .orElseThrow(() -> new AssertionError("Product " + productId + " was not found"));
        
        // 2. Perform the exact same numeric validation for both states
        assertEquals(expectedStock, currentProduct.getStock(),
            "Stock verification failed! Expected the database to reflect " + expectedStock);
        
        // 3. Optional: Add a logging context check to verify the correct business path 
        if("should remain".equalsIgnoreCase(expectationCondition)){
            System.out.println("Confirmed safety rule: Stock remained unchanged at " + currentProduct.getStock());    
        } else {
            System.out.println("Confirmed business role: Stock sucessfully scaled to " + currentProduct.getStock());
        }

        //assertEquals(expectedStock, updatedProduct.getStock(), "Database stock did not update correctly!");

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
