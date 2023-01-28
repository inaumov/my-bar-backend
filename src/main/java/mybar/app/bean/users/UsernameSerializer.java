package mybar.app.bean.users;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import mybar.app.obfuscation.IObfuscator;

import java.io.IOException;

public class UsernameSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String s, JsonGenerator generator, SerializerProvider serializers) throws IOException, JsonGenerationException {
        Class<?> jsonView = serializers.getActiveView();
        if (jsonView == View.AdminView.class) {
            // your custom serialization code here
            generator.writeString(IObfuscator.USERNAME_OBFUSCATOR.obfuscate(s));
        } else {
            generator.writeString(s);
        }
    }

}