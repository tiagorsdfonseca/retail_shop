package com.example.catalog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data; 
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Document(collection = "products") //Tells Spring to store this inside a mongodb database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product{
    @Id
    private String id;
    private String name;
    private Integer stock;
    private Double price;
    private Boolean eligibleForPromo;

    private Map<String, Object> attributes;
}