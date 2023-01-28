package mybar.events.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mybar.dto.RateDto;
import mybar.events.KafkaTestContext;
import mybar.events.common.api.RecordObject;
import mybar.events.consumers.RatesEventConsumer;
import mybar.service.rates.RatesService;
import mybar.web.config.JacksonConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.timeout;

@ContextConfiguration(
        classes = {
                JacksonConfiguration.class,
                RatesEventConsumerTest.Config.class,
                RatesEventConsumer.class
        }
)
public class RatesEventConsumerTest extends KafkaTestContext {

    @Autowired
    public KafkaTemplate<String, RecordObject<?>> template;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RatesService ratesServiceMock;

    @SpyBean
    private RatesEventConsumer ratesEventConsumer;
    @Captor
    private ArgumentCaptor<String> topicArgumentCaptor;
    @Captor
    private ArgumentCaptor<RecordObject<RateDto>> rateDtoArgumentCaptor;

    @SneakyThrows
    @Test
    public void testConsume() {
        File json = ResourceUtils.getFile("classpath:rated_cocktail_event.json");

        var recordObject = objectMapper.readValue(json, new TypeReference<RecordObject<RateDto>>() {
        });

        String key = "test1@cocktail8";

        var expected = new RateDto();
        expected.setCocktailId("cocktail8");
        expected.setRatedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(1639939320588L), ZoneId.systemDefault()));
        expected.setStars(10);

        template.send(topic, key, recordObject);

        //consumer
        Mockito.verify(ratesEventConsumer, timeout(2000L))
                .consume(
                        topicArgumentCaptor.capture(),
                        rateDtoArgumentCaptor.capture()
                );

        RecordObject<RateDto> payload = rateDtoArgumentCaptor.getValue();
        Assertions.assertThat(payload)
                .isNotNull();
        Assertions.assertThat(topicArgumentCaptor.getValue())
                .isEqualTo(key);
        testEvents(expected, payload.getValue());

        Mockito.verify(ratesServiceMock).persistRate(Mockito.eq("test1"), Mockito.eq(expected));
    }

    private void testEvents(RateDto expected, RateDto record) {
        Assertions.assertThat(record)
                .isNotNull();
        Assertions.assertThat(record)
                .isEqualTo(expected);
    }

    @TestConfiguration
    static class Config {

        @Bean
        RatesService ratesService() {
            return Mockito.mock(RatesService.class);
        }
    }

}
