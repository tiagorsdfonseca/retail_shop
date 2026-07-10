package com.example.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
//@EnableAutoConfiguration(exclude = { SimpleDiscoveryClientAutoConfiguration.class })
/*@ImportAutoConfiguration(exclude ={
	org.springframework.cloud.autoconfigure.RefreshAutoConfiguration.class
})*/
@TestPropertySource(properties = "catalog.service.url=http://localhost:8082")
@ActiveProfiles("test")
@Testcontainers
class OrderApplicationTests {

	@MockitoBean
	private KafkaTemplate<String, ?> kafkaTemplate;

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	@Test
	void contextLoads() {
	}

}
