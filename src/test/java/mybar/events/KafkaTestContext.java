package mybar.events;

import mybar.web.config.kafka.KafkaConsumerConfiguration;
import mybar.web.config.kafka.KafkaProducerConfiguration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ContextConfiguration(classes = {
        KafkaProducerConfiguration.class,
        KafkaConsumerConfiguration.class,
})
@SpringBootTest
@DirtiesContext
@Testcontainers
public class KafkaTestContext {

    @Container
    protected static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"))
            .withEnv(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    @Value("${my-bar.events.rates-topic-name}")
    protected String topic;

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

}