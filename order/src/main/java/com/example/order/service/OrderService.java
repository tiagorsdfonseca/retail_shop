package com.example.order.service;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.order.client.CatalogClient;
import com.example.order.dto.OrderRequest;
import com.example.order.event.OrderCreatedEvent;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.example.order.client.CatalogResponse;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final CatalogClient catalogClient;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate; //Injected messaging broker tool

    @Transactional
    public String createOrder(OrderRequest request){
        CatalogResponse product = catalogClient.getProductStock(request.getProductId());

        if(product.getStock() < request.getQuantity()){
            throw new IllegalArgumentException("OUT_OF_STOCK");
        }

        String generatedOrderId = UUID.randomUUID().toString();

        OrderCreatedEvent event = new OrderCreatedEvent(
            generatedOrderId,
            request.getProductId(),
            request.getQuantity(),
            "CREATED"
        );

        kafkaTemplate.send("order-events", event.getOrderId(), event);

        return "CREATED";
    }

}
