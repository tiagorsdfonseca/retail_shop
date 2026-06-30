package com.example.catalog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.catalog.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    //Inherits built-in methods like save(), findById(), and deleteById()
}