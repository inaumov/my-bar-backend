package mybar.web.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka.events")
public class PropertyHolder {

    private String topic;
    private String producerClientId;
    private String servers;
    private String consumerGroupId;
    private long pollTimeout;

}