package mybar.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        jacksonSettings.setObjectMapper(objectMapper());
        return jacksonSettings;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

}