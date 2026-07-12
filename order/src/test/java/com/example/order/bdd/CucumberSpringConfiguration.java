package com.example.order.bdd;

import com.example.order.client.CatalogClient;
import com.example.order.client.PaymentClient;
import com.example.order.config.MockClientConfig;

import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernateAutoConfiguration;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@CucumberContextConfiguration
// Spins up the application on a random port for integration testing
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)// Starts a WireMock server on port 8081 to act as a fake Catalog/payment APIs
@AutoConfigureWireMock(port=8081)
// Automatically spins up a background Kafka broker on a random test port
@EmbeddedKafka(partitions=1, topics = { "order-transactions" }, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092"})
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

    /*@MockitoBean
    private CatalogClient catalogClient;

    @MockitoBean
    private PaymentClient paymentClient;*/

    @Autowired
    protected TestRestTemplate restTemplate;

    //@Container
   // @ServiceConnection //Bridges dynamic JDBC URL, username, and password into
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    static {
        postgresContainer.start();
        kafkaContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
   
        registry.add("spring.kafka.bootstrap-servers",kafkaContainer::getBootstrapServers);
    }
}

