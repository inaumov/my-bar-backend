package mybar.events.consumer;

import common.events.api.RecordObject;
import lombok.SneakyThrows;
import mybar.KafkaEventsTestApplication;
import mybar.dto.RateDto;
import mybar.events.consumers.RatesEventConsumer;
import mybar.service.rates.RatesService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ContextConfiguration(
        classes = {
                RatesEventConsumerTest.Config.class,
                RatesEventConsumerTest.KafkaTestContainersConfiguration.class,
                RatesEventConsumer.class
        }
)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KafkaEventsTestApplication.class)
@DirtiesContext
@Testcontainers
public class RatesEventConsumerTest {

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Autowired
    public KafkaTemplate<String, String> template;

    @Value("${kafka.events.rates-topic-name}")
    private String topic;

    @Autowired
    private RatesService ratesServiceMock;

    @Autowired
    private RatesEventConsumer ratesEventConsumer;

    @SneakyThrows
    @Test
    public void testConsumer() {
        template.send(topic, "test1@cocktail8", "{\"timestamp\":1639939320588,\"value\":{\"@class\":\"mybar.dto.RateDto\",\"stars\":10}}");
        ratesEventConsumer.getLatch().await(10000, TimeUnit.MILLISECONDS);

        Assertions.assertThat(ratesEventConsumer.getLatch().getCount())
                .isEqualTo(0L);

        var expected = new RateDto();
        expected.setCocktailId("cocktail8");
        expected.setRatedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(1639939320588L), ZoneId.systemDefault()));
        expected.setStars(10);

        Mockito.verify(ratesServiceMock).persistRate(Mockito.eq("test1"), Mockito.eq(expected));
    }

    @TestConfiguration
    static class Config {

        @Bean
        RatesService ratesService() {
            return Mockito.mock(RatesService.class);
        }
    }

    @TestConfiguration
    static class KafkaTestContainersConfiguration {

        @Bean
        ConcurrentKafkaListenerContainerFactory<String, RecordObject<?>> kafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, RecordObject<?>> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());
            return factory;
        }

        @Bean
        public ConsumerFactory<String, RecordObject<?>> consumerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-bar-backend");
            return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(RecordObject.class));
        }

        @Bean
        public ProducerFactory<String, String> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, String> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }
    }

}
