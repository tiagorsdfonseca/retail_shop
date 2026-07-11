package com.example.order.service;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.order.client.CatalogClient;
import com.example.order.client.PaymentClient;
import com.example.order.client.CatalogResponse;
import com.example.order.dto.OrderRequest;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.repository.OrderRepository;
import com.example.order.event.OrderCreatedEvent;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final CatalogClient catalogClient;
    private final PaymentClient paymentClient;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate; //Injected messaging broker tool

    @Transactional
    public Order createOrder(OrderRequest request){
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();

        double calculatedTotalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for(var itemReq : request.getItems()){
            CatalogResponse product = catalogClient.getProductStock(itemReq.getProductId());

            if(product == null){
                throw new IllegalArgumentException("Product not found: "+ itemReq.getProductId());
            }

            if(product.getStock() < itemReq.getQuantity()){
                throw new IllegalStateException("OUT_OF_STOCK: "+ product.getName());
            }
           
            //Map to database item entity
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemReq.getProductId());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());

            calculatedTotalAmount += (product.getPrice() * itemReq.getQuantity());
            orderItems.add(orderItem);
        }

        //Save the order as 'PENDING'
        Order order = new Order();
        order.setId(orderId);
        order.setItems(orderItems);
        order.setTotalAmount(calculatedTotalAmount);
        order.setStatus("PENDING");
        orderRepository.save(order);

        //Process external payment
        PaymentClient.PaymentRequest paymentRequest = new PaymentClient.PaymentRequest(
            orderId, calculatedTotalAmount, "CREDIT_CARD"
        );

        PaymentClient.PaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);

        if(paymentResponse == null || "DECLINED".equals(paymentResponse.getStatus())) {
            order.setStatus("REJECTED");
            orderRepository.save(order);
            throw new IllegalStateException("Payment declined");
        }

        //Update status to APPROVED
        order.setStatus("APPROVED");
        orderRepository.save(order);

        //Stream the multi-item event to Kafka
        List<OrderCreatedEvent.ItemEvent> eventItems = order.getItems().stream()
        .map(i -> new OrderCreatedEvent.ItemEvent(i.getProductId(), i.getQuantity()))
        .collect(Collectors.toList());

        OrderCreatedEvent orderEvent = new OrderCreatedEvent(
            order.getId(),
            order.getStatus(),
            order.getTotalAmount(),
            eventItems
        );

        kafkaTemplate.send("order-transactions", orderId, orderEvent);
        
        return order;
    }

}
