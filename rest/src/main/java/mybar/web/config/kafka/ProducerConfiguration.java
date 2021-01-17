package mybar.web.config.kafka;

import lombok.Getter;
import lombok.Setter;
import mybar.messaging.producer.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ProducerConfiguration {

    private final PropertyHolder propertyHolder;

    @Autowired
    public ProducerConfiguration(PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    @Bean
    public KafkaMessageProducer messageProducer() {
        return new KafkaMessageProducer(propertyHolder.getTopic(), propertyHolder.getServers(), propertyHolder.getProducerClientId());
    }

}