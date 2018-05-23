package mybar.messaging;

import mybar.service.rates.RatesService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceMockProvider {
    @Bean
    RatesService ratesService() {
        return Mockito.mock(RatesService.class);
    }
}
