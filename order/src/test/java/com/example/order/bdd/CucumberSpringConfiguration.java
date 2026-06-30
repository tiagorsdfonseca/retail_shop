package com.example.order.bdd;

import com.example.order.client.CatalogClient;
import com.example.order.client.PaymentClient;
import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernateAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.example.order.bdd.config.TestClientConfig;
import org.springframework.context.annotation.Import;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.example.order.bdd.config.MockClientConfig;
import org.springframework.test.context.ActiveProfiles;
//import org.springframework.cloud.openfeign.FeignClient;


@CucumberContextConfiguration
// Spins up the application on a random port for integration testing
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)// Starts a WireMock server on port 8081 to act as a fake Catalog/payment APIs
@AutoConfigureWireMock(port=8081)
// Automatically spins up a background Kafka broker on a random test port
@EmbeddedKafka(partitions=1, topics = { "order-events" })
//@Import(TestClientConfig.class)
@ActiveProfiles("test")
@Import(MockClientConfig.class)
//Force Spring to completely drop any Feign configurations or clients from the scan tree
/*@ComponentScan(
    basePackages = "com.example.order",
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = FeignClient.class),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.order\\.config\\.Feign.*")
    }
)*/
public class CucumberSpringConfiguration {

    @MockitoBean
    private CatalogClient catalogClient;

    @MockitoBean
    private PaymentClient paymentClient;
    
}

