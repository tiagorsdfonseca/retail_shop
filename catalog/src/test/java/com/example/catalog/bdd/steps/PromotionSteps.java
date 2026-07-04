package com.example.catalog.bdd.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import com.example.catalog.bdd.CucumberSpringConfiguration;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;



public class PromotionSteps extends CucumberSpringConfiguration {

    
    @Autowired
    public TestRestTemplate restTemplate; //simulates API requests to our order service

    private ResponseEntity<String> response;
    
    @When("an admin activates a {double}% store-wide holiday markdown to product {string} with an eligibility attribute set to {boolean}")
    public void processPromotion(double discountToApply, String productId, boolean eligibleForPromo){
        //String payload = String.format("{\"productId\":\"%s\", \"discount\":%f, \"eligibleForPromo\":%b}", productId, discountToApply, eligibleForPromo);

        //Triggers our actual PromotionController endpoint
        //response = restTemplate.postForEntity("/promotions/activate", payload, String.class);
        Map<String, Object> payload = Map.of(
            "productId", productId,
            "discount", discountToApply,
            "eligibleForPromo", eligibleForPromo

        );

        response = restTemplate.postForEntity("/promotions/activate", payload, String.class);
    }

    @Then("the active retail price for {string} should calculate to {double}")
    public void verifyPromotionUpdate(String productId, double updatedPrice){
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertTrue(response.getBody().contains(String.format("\"productId\":\"%s\", \"updatedPrice\":%d", productId, updatedPrice)));
        //String expectedContent = String.format("\"productId\":\"%s\", \"updatedPrice\":%.2f", productId, updatedPrice);
        //assertTrue(expectedContent.getBody().contains(productId));
        String body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertTrue(body.contains(productId), "Response should contain product ID");
        assertTrue(body.contains(String.valueOf(updatedPrice)),"Response should contain updated price");
    }

    @Then("the active retail price for {string} should remain {double}")
    public void rejectPromotionUpdate(String productId, double oldPrice){
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertTrue(response.getBody().contains(String.format("\"productId\":\"%s\", \"oldPrice\":%d", productId, oldPrice)));
        String body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertTrue(body.contains(productId), "Response should not contain product ID");
        assertTrue(body.contains(String.valueOf(oldPrice)), "Response should retain the same price");

    }

    @When("an admin deactivates the {double}% store-wide holiday markdown to product {string} with an eligibility attribute set to {boolean}")
    public void processPromotionDeactivation(double discountToDiscard, String productId, boolean eligibleForPromo){
       // String payload = String.format("{\"productId\":\"%s\", \"discount\":%f, \"eligibleForPromo\":%b}", productId, discountToDiscard, eligibleForPromo);

        //Triggers our actual PromotionController endpoint
        //response = restTemplate.postForEntity("/promotions/activate", payload, String.class);

        Map<String, Object> payload = Map.of(
            "productId", productId,
            "status", "PROMOTION_DEACTIVATED"
        );

        response = restTemplate.postForEntity("/promotions/terminate",payload,String.class);
    }

    @When("the current date is {string}")
    @And("the product {string} has an eligibility attribute set to  {boolean}")
    @And("a {double}% store-wide holiday markdown is active with an expiration date of {date}")
    public void endPromotionDueToExpiredDate(String currentDate, String productId, boolean eligibleForPromo, double discountToDiscard, Date expirationDate){
        //String payload = String.format("{\"productId\":\"%s\", \"discount\":%f, \"eligibleForPromo\":%b}", productId, discountToDiscard, eligibleForPromo);

        //Triggers our actual PromotionController endpoint
        //response = restTemplate.postForEntity("/promotions/activate", payload, String.class);

        Map<String, Object> payload = Map.of(
            "productId", productId,
            "status", "EXPIRED"
        );

        response = restTemplate.postForEntity("/promotions/terminate", payload, String.class);
    }
    
    @Then("the active retail price for {string} should revert {double}")
    @And("the eligibility attribute is set to false")
    public void endPromotion(String productId, double revertedPrice,boolean eligibility){
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertTrue(response.getBody().contains(String.format("\"productId\":\"%s\", \"revertedPrice\":%d", productId, revertedPrice)));
        //eligibility=false;
        //assertTrue(response.getBody().contains(productId));
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains(productId));
        assertTrue(body.contains(String.valueOf(revertedPrice)));

    }


    

    
}
