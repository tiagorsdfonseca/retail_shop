package com.example.order.consumer;

import com.example.order.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderTransactionConsumer {

    @KafkaListener(
        topics = "order-transactions",
        groupId = "inventory-processing-group"
    )
    public void consumeOrderEvent(OrderCreatedEvent event){
        if(!"APPROVED".equals(event.getStatus())) {
            System.out.println("Processing skipped downstream. Transaction status: "+ event.getStatus());
            return;
        }

        System.out.println("===== DOWNSTREAM CONSUMER TRIPPED =====");
        System.out.println("Processing approved order ID: "+ event.getOrderId());
        System.out.println("Total Basket Value: $" + event.getTotalAmount());

        for(OrderCreatedEvent.ItemEvent item : event.getItems()){
            System.out.println(" -> Fulfilling " + item.getQuantity() + " units for Product ID: "+ item.getProductId());
        }
        // System.out.println("Fulfilling " + event.getQuantity() + " units for Product: "+ event.getProductId());
        System.out.println("================================");
    }
    
}
