package mybar.web.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "kafka.events")
public class PropertyHolder {
    @NotBlank
    private String topic;
    @NotBlank
    private String producerClientId;
    @NotBlank
    private String servers;
    @NotBlank
    private String consumerGroupId;

    @Min(5000)
    @Max(60000)
    private long pollTimeout;

}