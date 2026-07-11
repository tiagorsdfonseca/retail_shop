package com.example.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
  //  private String productId;
   // private Integer quantity;
   private String userId;
   private List<OrderItemRequest> items;
}
