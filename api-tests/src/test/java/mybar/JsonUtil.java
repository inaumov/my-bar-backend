package mybar;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonUtil {

    public String resourceAsString(String jsonFilePath) {
        InputStream resourceAsStream = getClass().getResourceAsStream(jsonFilePath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        JSONObject json = new JSONObject(new JSONTokener(bufferedReader));
        return json.toString();
    }

    public JSONObject resourceAsJSON(String jsonFilePath) {
        InputStream resourceAsStream = getClass().getResourceAsStream(jsonFilePath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        return new JSONObject(new JSONTokener(bufferedReader));
    }

}
