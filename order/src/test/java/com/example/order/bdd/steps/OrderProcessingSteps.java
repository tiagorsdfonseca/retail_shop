package com.example.order.bdd.steps;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import com.example.order.bdd.KafkaTestConsumer;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class OrderProcessingSteps{

    @Autowired
    public TestRestTemplate restTemplate; //simulates API requests to our order service

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    @Autowired
    private KafkaTestConsumer kafkaTestConsumer;

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

    @And("a Kafka message should be emmited to {string}")
    public void verifyKafkaMessageIsEmitted(String topic) throws InterruptedException{
        //Wait up to 5 seconds for the controller to assynchronously publish to the broker
        ConsumerRecord<String, Map<String, Object>> receivedRecord = kafkaTestConsumer.pollMessage(5);

        // Assert that we sucessfully captured a broadcasted event
        assertNotNull(receivedRecord, "Failed to capture a Kafka event on topic:" + topic);

        //Extract the JSON payload maps
        Map<String, Object> payload = receivedRecord.value();

        //Validate that the event data accurately matches the approved transaction
        assertNotNull(payload.get("orderId"));
        assertEquals("APPROVED", payload.get("status"));

        //Reset the queue to keep any subsequent scenario runs isolated
        kafkaTestConsumer.clear();


    }

    @And("no Kafka message should be emmited")
    public void verifyNoKafkaMessageIsEmitted(){
        //Wait briefly to confirm nothing rogue is published assynchronously
        try {
            Thread.sleep(1500);
        
        //Poll the queue
        ConsumerRecord<String, Map<String, Object>> receiveRecord = kafkaTestConsumer.pollMessage(1);

        // Assert that absolutely nothing was caught by our listener
        assertNull(receiveRecord, "An unexpected Kafka event was detected on the streaming cluster!");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}