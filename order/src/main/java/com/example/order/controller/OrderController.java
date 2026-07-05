package com.example.order.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> payload) {

        String productId = (String) payload.get("productId");
        Integer quantity = (Integer) payload.get("quantity");

        // Sad path validation (verifyRejectedTransaction & verifySucessfullTransaction)
        if(quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "REJECTED",
                "reason", "Invalid order quantity"
            ));
        }

        // Messaging Streamm Step (Matches: verifyKafkaMessageIsEmitted)
        // In the next step we will configure this to use your actual kafkaTemplate
        System.out.println("Pushing order event to Kafka cluster for product: "+ productId);
        

        //Happy Path approval (Matches: verifySucessfulTransaction)
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "productId", productId,
            "quantity", quantity,
            "status", "APPROVED"
        ));
    }
    
}
