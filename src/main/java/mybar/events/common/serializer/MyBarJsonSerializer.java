package mybar.events.common.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import mybar.events.common.api.RecordObject;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class MyBarJsonSerializer extends JsonSerializer<RecordObject<?>> {
    public MyBarJsonSerializer() {
        super();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
