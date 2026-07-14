package com.example.catalog.consumer;

import com.example.catalog.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.example.catalog.repository.ProductRepository;

@Component
public class OrderTransactionConsumer {

    private final ProductRepository productRepository;

    //Injecting the Catalog data access layer
    public OrderTransactionConsumer(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

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
       // System.out.println("Total Basket Value: $" + event.getTotalAmount());

        for(OrderCreatedEvent.ItemEvent item : event.getItems()){
          //  System.out.println(" -> Fulfilling " + item.getQuantity() + " units for Product ID: "+ item.getProductId());
          productRepository.findById(item.getProductId()).ifPresent(product -> {
            int currentStock = product.getStock() != null ? product.getStock() : 0;
            int newStock = currentStock - item.getQuantity();

            product.setStock(Math.max(0,newStock));
            productRepository.save(product);

            System.out.println(" -> Sucessfully fulfilled "+ item.getQuantity() + " units for Product: "+ product.getName());
          });
        }
        // System.out.println("Fulfilling " + event.getQuantity() + " units for Product: "+ event.getProductId());
        System.out.println("================================");
    }
    
}
