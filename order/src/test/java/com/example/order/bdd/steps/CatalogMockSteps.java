package com.example.order.bdd.steps;

import io.cucumber.java.en.Given;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CatalogMockSteps {
    @Given("the Catalog Service reports product {string} has {int} units in stock")
    public void the_catalog_service_products_has_units_in_stock(String productId, Integer stock){
        //WireMock intercepts the internal HTTP call from Order -> Catalog
        //and mocks a JSON response matching the database state we want to simulate.
        stubFor(get(urlEqualTo("/products/" + productId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type","application/json")
                    .withBody(String.format("{\"id\": \"%s\", \"stock\": %d}", productId, stock))));
    }
}