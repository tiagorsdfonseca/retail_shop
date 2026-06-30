package com.example.catalog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products") //Tells Spring to store this inside a mongodb database
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product{
    @Id
    private String id;
    private String name;
    private Integer stock;
    private Double price;
}