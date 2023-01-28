package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import mybar.api.bar.Measurement;

import java.io.IOException;
import java.util.Collection;

public class MeasurementsSerializer extends JsonSerializer<Collection<Measurement>> {

    @Override
    public void serialize(Collection<Measurement> values, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonGenerationException {
        generator.writeStartArray(values.size());
        if (!values.isEmpty()) {
            for (Measurement measurement : values) {
                generator.writeStartObject();
                generator.writeFieldName("value");
                generator.writeString(measurement.name());
                generator.writeFieldName("fullName");
                generator.writeString(measurement.getFullName());
                generator.writeEndObject();
            }
        }
        generator.writeEndArray();
    }

}