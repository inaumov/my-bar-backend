package mybar.rest.bar;

import com.google.common.io.CharStreams;
import io.restassured.http.ContentType;
import mybar.rest.Um;
import org.junit.Assert;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CocktailsApiTest {

    @Test
    public void testGetAllCocktails() {
        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.get("http://localhost:8089/api/bar/cocktails");
        Assert.assertEquals(200, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

    @Test
    public void testGetCocktailsByMenu() {
        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.get("http://localhost:8089/api/bar/cocktails" + "?filter=shots");
        Assert.assertEquals(200, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

    @Test
    public void testGetCocktailDetailsById() {
        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.get("http://localhost:8089/api/bar/cocktails/cocktail-000001");
        Assert.assertEquals(200, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

    @Test
    public void testAddNewCocktail() throws IOException {
        String resourceAsString = resourceAsString("/data/cocktails/new_cocktail_v1.json");

        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.contentType(ContentType.JSON).body(resourceAsString).post("http://localhost:8089/api/bar/cocktails");
        Assert.assertEquals(201, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

    @Test
    public void testUpdateExistedCocktail() throws IOException {
        String resourceAsString = resourceAsString("/data/cocktails/cocktail_v1.json");
        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.contentType(ContentType.JSON).body(resourceAsString).put("http://localhost:8089/api/bar/cocktails");
        Assert.assertEquals(202, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

    private String resourceAsString(String jsonFilePath) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(jsonFilePath);
        return CharStreams.toString(new InputStreamReader(resourceAsStream));
    }

    @Test
    public void testRemoveExistedCocktail() {
        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.TEST_USERNAME, Um.USER_PASS);
        Response res = basicAuth.delete("http://localhost:8089/api/bar/cocktails/" + "cocktail-000012");
        Assert.assertEquals(204, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

}
