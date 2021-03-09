package mybar.events.impl;

import mybar.events.api.RecordObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class MyBarEventConsumerTest {

    private static final String MY_KEY = "my@key";
    private static final String MY_TOPIC = "my_topic";

    @Test
    public void testConsumer() {
        // This is YOUR consumer object
        MyBarEventConsumer<String> myTestConsumer = new MyBarEventConsumer<>(MY_TOPIC, "localhost", "testGroupId", 10L) {
            @Override
            public void prepare(String key, RecordObject<String> recordObject) {
                //
            }

            @Override
            public void consume() {
                //
            }
        };
        // Inject the MockConsumer into your consumer
        // instead of using a KafkaConsumer
        MockConsumer<String, String> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        myTestConsumer.setConsumer(mockConsumer);

        Collection<TopicPartition> partitions = new ArrayList<>();
        TopicPartition topicPartition = new TopicPartition(MY_TOPIC, 0);
        partitions.add(topicPartition);
        mockConsumer.subscribe(Collections.singletonList(MY_TOPIC));
        mockConsumer.rebalance(partitions);

        HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(topicPartition, 0L);
        mockConsumer.updateBeginningOffsets(beginningOffsets);

        mockConsumer.addRecord(new ConsumerRecord<>("my_topic", 0, 0L, MY_KEY, "value0"));
        mockConsumer.addRecord(new ConsumerRecord<>("my_topic", 0, 1L, MY_KEY, "value1"));
        mockConsumer.addRecord(new ConsumerRecord<>("my_topic", 0, 2L, MY_KEY, "value2"));
        mockConsumer.addRecord(new ConsumerRecord<>("my_topic", 0, 3L, MY_KEY, "value3"));
        mockConsumer.addRecord(new ConsumerRecord<>("my_topic", 0, 4L, MY_KEY, "value4"));

        // This is where you run YOUR consumer's code
        // This code will consume from the Consumer and do your logic on it
        myTestConsumer.shutdownAfter(Duration.ofSeconds(5));
        myTestConsumer.poll();
    }

}
