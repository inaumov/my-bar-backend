package mybar.web.config.events;

import common.events.api.RecordObject;
import common.events.producer.MyBarEventProducer;
import mybar.dto.RateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class EventsConfiguration {

    private final KafkaTemplate<String, RecordObject<?>> kafkaTemplate;

    @Autowired
    public EventsConfiguration(KafkaTemplate<String, RecordObject<?>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public MyBarEventProducer<RateDto> myBarEventProducer() {
        return new MyBarEventProducer<>(kafkaTemplate);
    }

}