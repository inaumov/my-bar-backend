package mybar.messaging.consumer;

import mybar.messaging.ServiceMockProvider;
import mybar.service.rates.RatesService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServiceMockProvider.class, loader = AnnotationConfigContextLoader.class)
public class KafkaMessageConsumerTest {

    private static final String MY_KEY = "my@key";
    private static final String MY_TOPIC = "my_topic";

    @Autowired
    private RatesService ratesService;

    @Test
    public void testConsumer() throws IOException {
        // This is YOUR consumer object
        KafkaMessageConsumer myTestConsumer = new KafkaMessageConsumer(ratesService, MY_TOPIC, "localhost", "testGroupId", 10L);
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
        myTestConsumer.poll();

        // This just tests for exceptions
        // Somehow test what happens with the consume()
    }

}
