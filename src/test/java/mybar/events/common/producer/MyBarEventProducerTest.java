package mybar.events.common.producer;

import lombok.SneakyThrows;
import mybar.events.KafkaTestContext;
import mybar.events.common.api.RecordObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.Collections;

@ContextConfiguration(
        classes = {
                MyBarEventProducer.class
        }
)
public class MyBarEventProducerTest extends KafkaTestContext {

    @Autowired
    private MyBarEventProducer<TestEvent> target;

    @Autowired
    private ConsumerFactory<String, RecordObject<?>> consumerFactory;

    @SneakyThrows
    @Test
    public void testProducer() {
        Instant timeSent = target.send(topic, "userX", "testId", new TestEvent("aaa"));

        var singleRecord = KafkaTestUtils.getSingleRecord(createTestConsumer(), topic);
        var recordValue = singleRecord.value();

        Assertions.assertThat(timeSent)
                .isNotNull();
        Assertions.assertThat(timeSent.toEpochMilli())
                .isEqualTo(recordValue.getTimestamp());

        TestEvent testEvent = (TestEvent) recordValue.getValue();
        Assertions.assertThat(testEvent.getTestData())
                .isEqualTo("aaa");
        Assertions.assertThat(singleRecord.key()).
                isEqualTo("userX@testId");
    }

    Consumer<String, RecordObject<?>> createTestConsumer() {
        Consumer<String, RecordObject<?>> consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton(topic));
        return consumer;
    }

}