package com.example.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.kafka.core.KafkaTemplate;
@SpringBootTest
//@EnableAutoConfiguration(exclude = { SimpleDiscoveryClientAutoConfiguration.class })
/*@ImportAutoConfiguration(exclude ={
	org.springframework.cloud.autoconfigure.RefreshAutoConfiguration.class
})*/
@TestPropertySource(properties = "catalog.service.url=http://localhost:8082")
class OrderApplicationTests {

	@MockitoBean
	private KafkaTemplate<String, ?> kafkaTemplate;

	@Test
	void contextLoads() {
	}

}
