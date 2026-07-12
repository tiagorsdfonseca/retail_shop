package com.example.order.client;

//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

//@FeignClient(name = "catalog-service", url="${catalog.service.url:http://localhost:8081}")
@HttpExchange(url = "/products")
public interface CatalogClient {

    //@GetMapping("/products/{productId}")
     
    @GetExchange("/{productId}")
    CatalogResponse getProductStock(@PathVariable("productId") String productId);

    
}
