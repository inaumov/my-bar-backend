package mybar.web.config.kafka;

import lombok.Getter;
import lombok.Setter;
import mybar.messaging.consumer.KafkaMessageConsumer;
import mybar.service.rates.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ConsumerConfiguration {

    private final RatesService ratesService;
    private final PropertyHolder propertyHolder;

    @Autowired
    public ConsumerConfiguration(PropertyHolder propertyHolder, RatesService ratesService) {
        this.ratesService = ratesService;
        this.propertyHolder = propertyHolder;
    }

    @Bean(initMethod = "runConsumer")
    public KafkaMessageConsumer messageConsumer() {
        return new KafkaMessageConsumer(ratesService, propertyHolder.getTopic(), propertyHolder.getServers(), propertyHolder.getConsumerGroupId(), propertyHolder.getPollTimeout());
    }

}