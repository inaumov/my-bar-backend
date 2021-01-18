package mybar.rest.bar;

import com.google.common.io.CharStreams;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import mybar.rest.Um;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShelfApiTest {

    @Test
    public void testGetAllBottles() {
        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.get("http://localhost:8089/api/bar/shelf/bottles");
        Assert.assertEquals(200, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

    @Test
    public void testGetBottleById() {
        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.get("http://localhost:8089/api/bar/shelf/bottles/bottle-000001");
        Assert.assertEquals(200, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

    @Test
    public void testAddNewBottle() throws IOException {
        String resourceAsString = resourceAsString("/data/shelf/new_bottle_v1.json");

        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.contentType(ContentType.JSON).body(resourceAsString).post("http://localhost:8089/api/bar/shelf/bottles");
        Assert.assertEquals(201, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

    @Test
    public void testUpdateBottle() throws IOException {
        String resourceAsString = resourceAsString("/data/shelf/bottle_v1.json");
        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.contentType(ContentType.JSON).body(resourceAsString).put("http://localhost:8089/api/bar/shelf/bottles");
        Assert.assertEquals(202, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

    private String resourceAsString(String jsonFilePath) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(jsonFilePath);
        return CharStreams.toString(new InputStreamReader(resourceAsStream));
    }

    @Test
    public void testRemoveBottle() {
        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.delete("http://localhost:8089/api/bar/shelf/bottles/" + "bottle-000011");
        Assert.assertEquals(204, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

}
