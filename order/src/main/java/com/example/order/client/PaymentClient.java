package com.example.order.client;

//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@FeignClient(name = "payment-service", url = "${payment.service.url:http://localhost:8083}")
@HttpExchange(url = "{payment.gateway.url:http://localhost:8083}")
public interface PaymentClient {

    //@PostMapping("/payments")
    @PostExchange("/payments")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);

    //Data Transfer Objects (DTOs) representing the API contract --
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentRequest{
        private String orderId;
        private Double amount;
        private String paymentMethod;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentResponse{
        private String transactionId;
        private String status; // "SUCCESS", "DECLINED"
    }
}