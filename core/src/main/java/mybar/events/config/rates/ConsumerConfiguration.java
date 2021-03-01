package mybar.events.config.rates;

import lombok.Getter;
import lombok.Setter;
import mybar.events.RatesEventConsumer;
import mybar.events.api.IEventConsumer;
import mybar.events.config.PropertyHolder;
import mybar.service.rates.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Configuration
public class ConsumerConfiguration {

    private final RatesService ratesService;
    private final PropertyHolder propertyHolder;

    @Value("${kafka.events.rates.topic:}")
    @NotBlank
    private String ratesTopic;

    @NotBlank
    @Value("${kafka.events.rates.consumer_group_id:}")
    private String consumerGroupId;

    @Autowired
    public ConsumerConfiguration(RatesService ratesService, PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
        this.ratesService = ratesService;
    }

    @Bean(initMethod = "runConsumer")
    public IEventConsumer ratesEventConsumer() {
        return new RatesEventConsumer(ratesService, ratesTopic, propertyHolder.getServers(), consumerGroupId, propertyHolder.getPollTimeout());
    }

}