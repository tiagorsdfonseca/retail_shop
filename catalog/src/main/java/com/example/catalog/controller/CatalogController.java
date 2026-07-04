package com.example.catalog.controller;

import com.example.catalog.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class CatalogController {
    
   /*  @GetMapping("/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable("sku") String sku ){
        //Temporary harcoded response matching walking skeleton pattern
        Product mockProduct = new Product();
        mockProduct.setSku(sku);
        mockProduct.setName("Sample Product");
        mockProduct.setPrice(29.99);


        return ResponseEntity.ok(mockProduct);
    }*/

    @PostMapping("/{productId}/restock")
    public ResponseEntity<String> restockProduct(
        @PathVariable("productId") String productId,
        @RequestBody Map<String,Integer> payload){

            Integer quantity = payload.get("quantity");

            if(quantity==null && quantity <=0){
                return ResponseEntity.badRequest().body("Invalid stock increment");
            }

            return ResponseEntity.ok("Stock updated sucessfully");
    }

}