package mybar.events.config.rates;

import lombok.Getter;
import lombok.Setter;
import mybar.events.api.IEventProducer;
import mybar.events.config.PropertyHolder;
import mybar.events.impl.MyBarEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Configuration
public class ProducerConfiguration {

    private final PropertyHolder propertyHolder;

    @Value("${kafka.events.rates.topic:}")
    @NotBlank
    private String ratesTopic;

    @Value("${kafka.events.rates.producer_client_id:}")
    @NotBlank
    private String producerClientId;

    @Autowired
    public ProducerConfiguration(PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    @Bean
    public IEventProducer ratesEventProducer() {
        return new MyBarEventProducer(ratesTopic, propertyHolder.getServers(), producerClientId);
    }

}