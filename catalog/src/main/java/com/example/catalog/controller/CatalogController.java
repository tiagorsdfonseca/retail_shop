package com.example.catalog.controller;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import com.example.catalog.service.CatalogService;
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

   // @Autowired
   // private ProductRepository productRepository;
    
   //Comm
   /*  @GetMapping("/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable("sku") String sku ){
        //Temporary harcoded response matching walking skeleton pattern
        Product mockProduct = new Product();
        mockProduct.setSku(sku);
        mockProduct.setName("Sample Product");
        mockProduct.setPrice(29.99);


        return ResponseEntity.ok(mockProduct);
    }*/

   private final CatalogService catalogService;

   public CatalogController(CatalogService catalogService){
    this.catalogService = catalogService;
   }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable("productId") String productId){

        return catalogService.getProductById(productId)
            .map(ResponseEntity::ok)
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

            return catalogService.restockProduct(productId, quantity)
                    .map(updatedProduct -> ResponseEntity.ok("Stock updated sucessfully"))
                    .orElse(ResponseEntity.notFound().build());

           }

    //Quick helper endpoint to create a product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        return ResponseEntity.ok(catalogService.saveProduct(product));
    }

}