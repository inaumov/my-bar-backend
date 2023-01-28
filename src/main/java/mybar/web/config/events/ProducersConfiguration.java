package mybar.web.config.events;

import mybar.events.common.api.RecordObject;
import mybar.events.common.producer.MyBarEventProducer;
import mybar.dto.RateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ProducersConfiguration {

    private final KafkaTemplate<String, RecordObject<?>> kafkaTemplate;

    @Autowired
    public ProducersConfiguration(KafkaTemplate<String, RecordObject<?>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public MyBarEventProducer<RateDto> ratesEventProducer() {
        return new MyBarEventProducer<>(kafkaTemplate);
    }

}