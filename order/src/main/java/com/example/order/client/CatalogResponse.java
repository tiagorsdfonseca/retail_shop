package com.example.order.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CatalogResponse{
        private String id;
        private String name;
        private Integer stock;
        private Double price;
    }
