package com.example.order.bdd.config;

import com.example.order.client.CatalogClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    @Value("${catalog.service.url:http://localhost:8081}")
    private String catalogServiceUrl;

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
    
}
