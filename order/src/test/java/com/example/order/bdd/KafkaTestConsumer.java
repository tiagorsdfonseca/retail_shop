package com.example.order.bdd;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.Map;

@Component
public class KafkaTestConsumer {
    
    //A thread-safe queue to capture messages emitted by the controller
    private final BlockingQueue<ConsumerRecord<String, Map<String, Object>>> records = new LinkedBlockingQueue<>();
    
    @KafkaListener(topics = "order-transactions", groupId = "order-test-group")
    public void listen(ConsumerRecord<String, Map<String,Object>> record) {
        records.add(record);
    }

    public ConsumerRecord<String, Map<String, Object>> pollMessage(long timeOutSeconds) throws InterruptedException{
        return records.poll(timeOutSeconds, TimeUnit.SECONDS);
    }


public void clear(){
    records.clear();
 }   
}
