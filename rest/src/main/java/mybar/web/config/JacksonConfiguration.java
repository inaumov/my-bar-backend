package mybar.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperFactoryBean jacksonSettings() {
        Jackson2ObjectMapperFactoryBean jacksonSettings = new Jackson2ObjectMapperFactoryBean();
        jacksonSettings.setFailOnUnknownProperties(false);
        jacksonSettings.setFailOnEmptyBeans(true);
        return jacksonSettings;
    }

}