package mybar.rest.bar;

import io.restassured.http.ContentType;
import mybar.rest.ApiTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

import static mybar.CommonPaths.API_PATH;
import static mybar.rest.Constants.TEST_USERNAME;
import static mybar.rest.Constants.USER_PASS;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

@TestMethodOrder(MethodOrderer.MethodName.class)
class ShelfApiTest extends ApiTest {

    public static final String RESOURCE_ID_PATTERN = "^[a-z]+(-[a-zA-Z0-9]{6}+)*$";
    public static Pattern RESOURCE_ID = Pattern.compile(RESOURCE_ID_PATTERN);

    @BeforeEach
    void setUp() {
        runSql("/bottle.sql");
    }

    @Test
    void testGetAllBottles() {
        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .get(API_PATH + "shelf/bottles")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("size()", Matchers.greaterThan(1));
    }

    @Test
    void testGetBottleById() {
        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .pathParam("id", "bottle-000001")
                .get(API_PATH + "shelf/bottles/{id}")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("id", Matchers.not(Matchers.empty()));
    }

    @Test
    void testAddNewBottle() {

        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/shelf/new_bottle_v1.json");
        resourceAsJSON.put("brandName", resourceAsJSON.getString("brandName") + " - " + RandomStringUtils.randomAlphabetic(6));

        String id = givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .contentType(ContentType.JSON)
                .and()
                .body(resourceAsJSON.toString())
                .post(API_PATH + "shelf/bottles")
                .then()
                .statusCode(201)
                .body("id", matchesPattern(RESOURCE_ID))
                .extract()
                .path("id");

        Assert.notNull(id, "Id should be created");
    }

    @Test
    void testUpdateBottle() {

        String resourceAsString = jsonUtil.resourceAsString("/data/shelf/bottle_v1.json");

        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .contentType(ContentType.JSON)
                .and()
                .body(resourceAsString)
                .put(API_PATH + "shelf/bottles")
                .then()
                .statusCode(202)
                .body("id", matchesPattern(RESOURCE_ID));
    }

    @Test
    void testRemoveBottle() {
        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .pathParam("id", "bottle-000011")
                .delete(API_PATH + "shelf/bottles/{id}")
                .then()
                .statusCode(204);
    }

}
