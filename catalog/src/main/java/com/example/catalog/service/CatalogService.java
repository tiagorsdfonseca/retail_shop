package com.example.catalog.service;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CatalogService {

    private final ProductRepository productRepository;

    public CatalogService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Optional<Product> getProductById(String id){
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }


    //Handles the business logic for restocking a product safely
    // Returns the updated product if sucessful, or empty if the product doesn't

    public Optional<Product> restockProduct(String productId, int quantity){
        if (quantity <= 0) {
            throw new IllegalArgumentException("Invalid stock increment");
        }

        return productRepository.findById(productId)
                .map(product -> {
                    product.setStock(product.getStock() + quantity);
                    return productRepository.save(product);
                });
    }
}
