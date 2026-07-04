package com.example.catalog.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/promotions")
public class PromotionController {
    
    @PostMapping("/activate")
    public ResponseEntity<Map<String,Object>> activatePromotion(@RequestBody Map<String, Object> payload){
        String productId = (String) payload.get("productId");
        Double discount = ((Number) payload.get("discount")).doubleValue();
        Boolean eligibleForPromo = (Boolean) payload.get("eligibleForPromo");

        if(!eligibleForPromo) {
            return ResponseEntity.ok(Map.of(
                "productId", productId,
                "oldPrice", 100.0,
                "status", "PROMOTION_REJECTED"
            ));
        }

        //Calculate a mock updated price (base price 100.0 minus discount)
        double basePrice = 100.0;
        double updatedPrice = basePrice * (1 - (discount / 100.0));

        return ResponseEntity.ok(Map.of(
            "productId", productId,
            "updatedPrice", updatedPrice,
            "status", "PROMOTION_ACTIVE"
        ));
    }

    @PostMapping("/terminate")
    public ResponseEntity<Map<String,Object>> terminatePromotion(@RequestBody Map<String, Object> payload){

        String productId = (String) payload.get("productId");

        String reason = (String) payload.getOrDefault("status", "DEACTIVATED");

        return ResponseEntity.ok(Map.of(

            "productId", productId,
            "revertedPrice", 100.0,
            "elegibleForPromo", false,
            "status", reason
        ));
    }
}
