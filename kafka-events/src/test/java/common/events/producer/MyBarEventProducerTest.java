package common.events.producer;

import common.events.api.RecordObject;
import common.events.serializer.MyBarJsonSerializer;
import lombok.SneakyThrows;
import mybar.KafkaEventsTestApplication;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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

    @SneakyThrows
    @Test
    public void testConsumer() {
        eventProducer.send(topic, "userX", "testId", new TestEvent("aaa"));
    }

    @TestConfiguration
    static class KafkaTestContainersConfiguration {

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