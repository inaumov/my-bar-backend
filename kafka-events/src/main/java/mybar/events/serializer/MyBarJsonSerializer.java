package mybar.events.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class MyBarJsonSerializer extends JsonSerializer<Object> {
    public MyBarJsonSerializer() {
        super();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
