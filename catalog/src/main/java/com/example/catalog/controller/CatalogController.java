package com.example.catalog.controller;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class CatalogController {

    @Autowired
    private ProductRepository productRepository;
    
   /*  @GetMapping("/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable("sku") String sku ){
        //Temporary harcoded response matching walking skeleton pattern
        Product mockProduct = new Product();
        mockProduct.setSku(sku);
        mockProduct.setName("Sample Product");
        mockProduct.setPrice(29.99);


        return ResponseEntity.ok(mockProduct);
    }*/

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable("productId") String productId){

        return productRepository.findById(productId)
            .map(product -> ResponseEntity.ok(product))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{productId}/restock")
    public ResponseEntity<String> restockProduct(
        @PathVariable("productId") String productId,
        @RequestBody Map<String,Integer> payload){

            Integer quantity = payload.get("quantity");

            if(quantity==null || quantity <=0){
                return ResponseEntity.badRequest().body("Invalid stock increment");
            }

            //Fetch the document from MongoDB
            Product product = productRepository.findById(productId)
                .orElse(null);

            if(product == null){
                return ResponseEntity.notFound().build();
            }

            // Perform the real business calculation
            product.setStock(product.getStock() + quantity);

            //Persist the updated state back into the collection
            productRepository.save(product);

            return ResponseEntity.ok("Stock updated sucessfully");
    }

}