package mybar.rest.bar;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import mybar.JsonUtil;
import mybar.rest.Um;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ShelfApiTest {

    public static final String RESOURCE_ID_PATTERN = "^[a-z]+(-[a-zA-Z0-9]{6}+)*$";
    public static Pattern RESOURCE_ID = Pattern.compile(RESOURCE_ID_PATTERN);

    private final JsonUtil jsonUtil = new JsonUtil();

    private static String bottleId;

    private static RequestSpecification givenAuthenticated() {
        return RestAssured
                .given()
                .auth()
                .preemptive()
                .basic(Um.TEST_USERNAME, Um.USER_PASS);
    }

    @Test
    public void testGetAllBottles() {
        givenAuthenticated()
                .get("http://localhost:8089/api/bar/shelf/bottles")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("size()", Matchers.greaterThan(1));
    }

    @Test
    public void testGetBottleById() {
        givenAuthenticated()
                .when()
                .pathParam("id", "bottle-000001")
                .get("http://localhost:8089/api/bar/shelf/bottles/{id}")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("id", Matchers.not(Matchers.empty()));
    }

    @Test
    public void testAddNewBottle() {

        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/shelf/new_bottle_v1.json");
        resourceAsJSON.put("brandName", resourceAsJSON.get("brandName") + " - " + RandomStringUtils.randomAlphabetic(6));

        String id = givenAuthenticated()
                .when()
                .contentType(ContentType.JSON)
                .and()
                .body(resourceAsJSON.toString())
                .post("http://localhost:8089/api/bar/shelf/bottles")
                .then()
                .statusCode(201)
                .body("id", matchesPattern(RESOURCE_ID))
                .extract()
                .path("id");

        ShelfApiTest.bottleId = id;
    }

    @Test
    public void testUpdateBottle() {

        String resourceAsString = jsonUtil.resourceAsString("/data/shelf/bottle_v1.json");

        givenAuthenticated()
                .when()
                .contentType(ContentType.JSON)
                .and()
                .body(resourceAsString)
                .put("http://localhost:8089/api/bar/shelf/bottles")
                .then()
                .statusCode(202)
                .body("id", matchesPattern(RESOURCE_ID));
    }

    @Test
    public void testRemoveBottle() {
        givenAuthenticated()
                .when()
                .pathParam("id", bottleId)
                .delete("http://localhost:8089/api/bar/shelf/bottles/{id}")
                .then()
                .statusCode(204);
    }

}
