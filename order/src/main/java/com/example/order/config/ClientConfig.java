package com.example.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.example.order.client.CatalogClient;
import com.example.order.client.PaymentClient;

@Configuration
public class ClientConfig {

    @Value("${catalog.service.url:http://localhost:8081}")
    private String catalogServiceUrl;

    @Value("${payment.gateway.url:http://localhost:8084}")
    private String paymentUrl;

    @Bean
    public CatalogClient catalogClient(){
        RestClient restClient = RestClient.builder()
            .baseUrl(catalogServiceUrl)
            .build();
        
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(adapter)
            .build();

        return factory.createClient(CatalogClient.class);
    }

    @Bean
    public PaymentClient paymentClient(){
        RestClient restClient = RestClient.builder().baseUrl(paymentUrl).build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(PaymentClient.class);
    }
    
}
