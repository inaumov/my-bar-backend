package mybar.events.impl;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyBarEventProducerTest {

    private static final String MY_KEY = "my@key";
    private static final String MY_TOPIC = "my_topic";

    @Test
    public void testProducer() {
        MyBarEventProducer kafkaMessageProducer = new MyBarEventProducer(MY_TOPIC, "localhost", "clientId");

        List<ProducerRecord<String, Object>> history = new ArrayList<>();
        history.addAll(sendAndGetProducerRecords(kafkaMessageProducer, MY_KEY, "value0"));
        history.addAll(sendAndGetProducerRecords(kafkaMessageProducer, MY_KEY, "value1"));
        history.addAll(sendAndGetProducerRecords(kafkaMessageProducer, MY_KEY, "value2"));
        history.addAll(sendAndGetProducerRecords(kafkaMessageProducer, MY_KEY, "value3"));
        history.addAll(sendAndGetProducerRecords(kafkaMessageProducer, MY_KEY, "value4"));

        List<ProducerRecord<String, String>> expected = Arrays.asList(
                new ProducerRecord<>(MY_TOPIC, MY_KEY, "value0"),
                new ProducerRecord<>(MY_TOPIC, MY_KEY, "value1"),
                new ProducerRecord<>(MY_TOPIC, MY_KEY, "value2"),
                new ProducerRecord<>(MY_TOPIC, MY_KEY, "value3"),
                new ProducerRecord<>(MY_TOPIC, MY_KEY, "value4"));

        Assertions.assertEquals(expected, history);
    }

    private List<ProducerRecord<String, Object>> sendAndGetProducerRecords(MyBarEventProducer kafkaMessageProducer, String key, String object) {
        MockProducer<String, Object> producer = new MockProducer<>(true, new StringSerializer(), new JsonSerializer<>());
        kafkaMessageProducer.setProducer(producer);
        kafkaMessageProducer.send(key, object);
        return producer.history();
    }

}