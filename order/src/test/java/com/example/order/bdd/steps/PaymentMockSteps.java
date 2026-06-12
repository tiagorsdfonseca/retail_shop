package com.example.order.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class PaymentMockSteps {

    // Given an order has been created with a total amount of 45.00
    // And the payment gateway accepts the transaction with authorization "AUTH-12345"
    @Given("an order has been created with a total amount of {double}")
    @And("the payment gateway accepts the transaction with authorization {string}")
    public void stubSucessfulPayment(double amount, String authCode) {
        // WireMock intercepts the internal HTTP call from Order -> Payment
        // and mocks a JSON response indicating approval for the specified card number.
        stubFor(post(urlEqualTo("/payments"))
                .withRequestBody(containing(authCode))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"approved\"}")));
    }

    // the payment gateway rejects the transaction with code {string}
    @Given("an order has been created with a total amount of {double}")
    @And("the payment gateway rejects the transaction with code {string}")
    public void stubDeclinedPayment(double amount, String errorCode) {
        stubFor(post(urlEqualTo("/v1/payments"))
                .willReturn(aResponse()
                        .withStatus(402)
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.format("{\"status\":\"DECLINED\", \"error\":\"%s\"}", errorCode))));
    }

    
    @Given("an order has been created with a total amount of {double}")
    @And("the payment fails to respond within {double} seconds")
    public void stubPaymentTimeOut(double amount, double timeoutSeconds) {
        stubFor(post(urlEqualTo("/v1/payments"))
                .willReturn(aResponse()
                        .withStatus(504)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"TIMEOUT\", \"error\":\"Payment gateway did not respond in time\"}")));
    }



}