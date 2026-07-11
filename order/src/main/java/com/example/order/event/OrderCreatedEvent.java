package com.example.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    
    private String orderId;
    private String status;
    private Double totalAmount;
    private List<ItemEvent> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemEvent {
        private String productId;
        private Integer quantity;
    }
}
