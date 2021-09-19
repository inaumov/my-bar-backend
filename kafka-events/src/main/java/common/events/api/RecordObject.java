package common.events.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@AllArgsConstructor(staticName = "of")
@Data
@NoArgsConstructor
@ToString
public class RecordObject<T> {
    public long timestamp;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    public T value;
}
