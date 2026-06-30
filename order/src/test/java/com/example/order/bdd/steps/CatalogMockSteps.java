package com.example.order.bdd.steps;

import io.cucumber.java.en.Given;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import org.mockito.Mockito;
import com.example.order.client.CatalogClient;
import com.example.order.client.CatalogResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class CatalogMockSteps {

    @Autowired
    private CatalogClient catalogClient;

    @Given("the Catalog Service reports product {string} has {int} units in stock")
    public void the_catalog_service_products_has_units_in_stock(String productId, Integer stock){
        //WireMock intercepts the internal HTTP call from Order -> Catalog
        //and mocks a JSON response matching the database state we want to simulate.
        stubFor(get(urlEqualTo("/products/" + productId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type","application/json")
                    .withBody(String.format("{\"id\": \"%s\", \"stock\": %d}", productId, stock))));

        //Mockito bean fetching data from Wiremock stub
        Mockito.when(catalogClient.getProductStock(productId))
            .thenAnswer(invocation -> {
                //Connects directly to port 8081 (used by WireMock)
                RestTemplate restTemplate = new RestTemplate();
                //return restTemplate.getForObject("http://localhost:8081/productId");
                return restTemplate.getForObject("http://localhost:8081/products/" + productId, CatalogResponse.class);
            });
    }
}