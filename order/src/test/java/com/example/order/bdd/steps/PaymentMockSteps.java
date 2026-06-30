package com.example.order.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import org.springframework.web.client.RestTemplate;
import com.example.order.client.PaymentClient;
import com.example.order.client.PaymentClient.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class PaymentMockSteps {

        @Autowired
        private PaymentClient paymentClient;

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
        
        when(paymentClient.processPayment(any()))
                .thenAnswer(invocation ->  {
                        //Routes the call through WireMock to verify data formats
                        RestTemplate restTemplate = new RestTemplate();
                        return restTemplate.postForObject("http://localhost:8081/payments", null, PaymentResponse.class);
                });
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
        
        when(paymentClient.processPayment(any()))
                .thenAnswer(invocation -> {
                        RestTemplate restTemplate = new RestTemplate();
                        return restTemplate.postForObject("http://localhost:8081/payments", null, PaymentResponse.class);
                });
   
        }

    
    @Given("an order has been created with a total amount of {double}")
    @And("the payment fails to respond within {double} seconds")
    public void stubPaymentTimeOut(double amount, double timeoutSeconds) {
        stubFor(post(urlEqualTo("/v1/payments"))
                .willReturn(aResponse()
                        .withStatus(504)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"TIMEOUT\", \"error\":\"Payment gateway did not respond in time\"}")));
    
    
        when(paymentClient.processPayment(any()))
                .thenThrow(new RuntimeException("Payment Gateway Timeout (HTTP 504)"));
        }



}