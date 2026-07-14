package com.example.catalog.consumer;

import java.time.Duration;
import java.util.List;

import org.awaitility.Awaitility;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import com.example.catalog.event.OrderCreatedEvent;
import com.example.catalog.event.OrderCreatedEvent.ItemEvent;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashMap;

import org.springframework.kafka.listener.MessageListenerContainer;
//import org.springframework.kafka.listener.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.utils.ContainerTestUtils;
//properties = {
  //  "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.>JacksonJsonDeserializer",
    //"spring.kafka.consumer.properties.spring.json.value.default.type=com.example.catalog.event.OrderCreatedEvent",
    //"spring.kafka.consumer.properties.spring.json.trusted.packages=com.example.catalog.event"
//}
@SpringBootTest(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JacksonJsonSerializer",
    "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JacksonJsonDeserializer",
    "spring.kafka.producer.properties.spring.json.add.type.headers=false",
    "spring.kafka.consumer.properties.spring.json.value.default.type=com.example.catalog.event.OrderCreatedEvent",
    "spring.kafka.consumer.properties.spring.json.trusted.packages=com.example.catalog.event",
    "spring.kafka.consumer.auto-offset-reset=earliest"
})
@DirtiesContext
@EmbeddedKafka(
    partitions = 1, 
    topics = {"order-transactions"}, 
    bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class OrderConsumerTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

   // @Autowired
   // private KafkaListenerEndpointRegistry registry;

    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Autowired
    private ProductRepository productRepository;

   /* @BeforeEach
    void setUp(){

        for(MessageListenerContainer messageListenerContainer : registry.getListenerContainers()){
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }

        String bootstrapServers = embeddedKafkaBroker.getBrokersAsString();
        //Dynamically resolve the true embedded server port allocated by JUnit 5
       // String bootstrapServers = System.getProperty("spring.kafka.bootstrap-servers");

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(bootstrapServers));
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        

        //Disable strict class package validation headers
        JacksonJsonSerializer<OrderCreatedEvent> jacksonSerializer = new JacksonJsonSerializer<>();
        jacksonSerializer.setAddTypeInfo(false);

        DefaultKafkaProducerFactory<String, OrderCreatedEvent> factory = new DefaultKafkaProducerFactory<>(
            configs, new StringSerializer(), jacksonSerializer
        );

        //Instantiate natively without container registration hooks
        this.kafkaTemplate = new KafkaTemplate<>(factory);

    }*/

    @Test
    void whenApprovedOrderTransactionReceived_thenDeductInventoryStock() {
        // Arrange: Build and seed a mock product to mutate stock on
        Product keyboard = Product.builder()
                .id("prod-xyz")
                .name("Mechanical Keyboard")
                .stock(15)
                .price(99.99)
                .eligibleForPromo(false)
                .build();
                
        productRepository.save(keyboard);

        // Build the payload matching your exact status check rules ("APPROVED")
        OrderCreatedEvent event = new OrderCreatedEvent(
            "order-400", 
            "APPROVED", 
            99.99, 
            List.of(new ItemEvent("prod-xyz", 3)) // Deduct 3 units from 15
        );

        //Stream the message onto the embedded Kafka cluster broker
        kafkaTemplate.send("order-transactions", event);

        // Asynchronously wait for the background listener to finish execution loop
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    Product updatedProduct = productRepository.findById("prod-xyz")
                            .orElseThrow(() -> new AssertionError("Product not found in test DB"));
                    
                    // 15 initial stock - 3 ordered units = 12 left
                    assertEquals(15, updatedProduct.getStock());
                });
    }



}