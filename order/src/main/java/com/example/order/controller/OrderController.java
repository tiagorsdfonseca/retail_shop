package com.example.order.controller;

import java.util.Map;

import com.example.order.client.CatalogClient;
import com.example.order.client.PaymentClient;
import com.example.order.client.CatalogResponse;
import com.example.order.dto.OrderRequest;
import com.example.order.repository.OrderRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;


import com.example.order.model.Order;

import java.util.UUID;



@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private CatalogClient catalogClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> payload) {

        String productId = (String) payload.get("productId");
        Integer quantity = (Integer) payload.get("quantity");
        Double amount = 45.00; 

        // Real Service-to-Serviice call over HTTP:
     // During testing, this hits WireMock. In production, this hits the CatalogResponse
     CatalogResponse catalog = catalogClient.getProductStock(productId);

        // Sad path validation (verifyRejectedTransaction & verifySucessfullTransaction)
        if(catalog == null ) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "REJECTED",
                "reason", "Invalid order quantity"
            ));
        }

        //Creating the database record with a 'PENDING' state
        Order order = new Order();
        order.setId("ORD-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setStatus("PENDING");
        orderRepository.save(order);

        // Messaging Streamm Step (Matches: verifyKafkaMessageIsEmitted)
        // In the next step we will configure this to use your actual kafkaTemplate
       // System.out.println("Pushing order event to Kafka cluster for product: "+ productId);
        
       PaymentClient.PaymentRequest paymentRequest = new PaymentClient.PaymentRequest("ORD-999", amount, "CREDIT_CARD");
       PaymentClient.PaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);

       if(paymentResponse == null || "DECLINED".equals(paymentResponse.getStatus())){
        return ResponseEntity.badRequest().body(Map.of("status","REJECTED", "error", "Payment declined"));
       }

       order.setStatus("APPROVED");
       orderRepository.save(order);

       Map<String, Object> orderEvent = Map.of(
            "orderId", order.getId(),
            "productId", order.getProductId(),
            "quantity", order.getQuantity(),
            "status", order.getStatus()
       );
       kafkaTemplate.send("order-transactions", productId,orderEvent);

        //Happy Path approval (Matches: verifySucessfulTransaction)
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
           "orderId", order.getId(),
            "productId", order.getProductId(),
            "status", order.getStatus()
        ));
    }
    
}
