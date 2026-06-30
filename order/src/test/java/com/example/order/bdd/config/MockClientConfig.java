package com.example.order.bdd.config;


import com.example.order.client.CatalogClient;
import com.example.order.client.PaymentClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MockClientConfig {

    @Bean
    //@Primary //Forces Spring to prioritize this Mockito mock over the real Feign Client Factory
    public CatalogClient catalogClient(){
        return Mockito.mock(CatalogClient.class);
    }

    @Bean
    //@Primary
    public PaymentClient paymentClient(){
        return Mockito.mock(PaymentClient.class);
    }
    
}
