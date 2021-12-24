package common.events.producer;

import common.events.api.RecordObject;
import common.events.serializer.MyBarJsonSerializer;
import lombok.SneakyThrows;
import mybar.KafkaEventsTestApplication;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(
        classes = {
                MyBarEventProducerTest.KafkaTestContainersConfiguration.class,
                MyBarEventProducer.class
        }
)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KafkaEventsTestApplication.class)
@DirtiesContext
@Testcontainers
public class MyBarEventProducerTest {

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Value("${kafka.events.rates-topic-name}")
    private String topic;

    @Autowired
    private MyBarEventProducer<TestEvent> eventProducer;

    @Autowired
    private ConsumerFactory<String, RecordObject<?>> consumerFactory;

    @SneakyThrows
    @Test
    public void testProducer() {
        eventProducer.send(topic, "userX", "testId", new TestEvent("aaa"));

        var singleRecord = KafkaTestUtils.getSingleRecord(createTestConsumer(), topic);
        var recordValue = singleRecord.value();
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

    @TestConfiguration
    static class KafkaTestContainersConfiguration {

        @Bean
        public ConsumerFactory<String, RecordObject<?>> consumerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-bar-backend");
            return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(RecordObject.class));
        }

        @Bean
        public ProducerFactory<String, RecordObject<?>> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MyBarJsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, RecordObject<?>> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }
    }

}