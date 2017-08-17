package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import mybar.UnitOfMeasurement;

import java.io.IOException;
import java.util.Collection;

public class UnitOfMeasurementsSerializer extends JsonSerializer<Collection<UnitOfMeasurement>> {

    @Override
    public void serialize(Collection<UnitOfMeasurement> value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (value.isEmpty()) {
            return;
        }
        generator.writeStartArray(value.size());
        for (UnitOfMeasurement unitOfMeasurement : value) {
            generator.writeStartObject();
            generator.writeFieldName("unit");
            generator.writeString(unitOfMeasurement.name());
            generator.writeFieldName("fullName");
            generator.writeString(unitOfMeasurement.getFullName());
            generator.writeEndObject();
        }
        generator.writeEndArray();
    }

}