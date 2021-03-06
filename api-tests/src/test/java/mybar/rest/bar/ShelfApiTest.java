package mybar.rest.bar;

import io.restassured.RestAssured;
import io.restassured.authentication.OAuthSignature;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import mybar.JsonUtil;
import mybar.OAuthAuthenticator;
import mybar.rest.Um;
import mybar.spring.ApiTestsContextConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

import static mybar.CommonPaths.API_PATH;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApiTestsContextConfiguration.class}, loader = AnnotationConfigContextLoader.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ShelfApiTest {

    public static final String RESOURCE_ID_PATTERN = "^[a-z]+(-[a-zA-Z0-9]{6}+)*$";
    public static Pattern RESOURCE_ID = Pattern.compile(RESOURCE_ID_PATTERN);
    private static String bottleId;
    private final JsonUtil jsonUtil = new JsonUtil();
    @Autowired
    private OAuthAuthenticator authenticator;

    private RequestSpecification givenAuthenticated() {

        final String accessToken = authenticator.getAccessToken(Um.TEST_USERNAME, Um.USER_PASS);

        return RestAssured
                .given()
                .auth()
                .oauth2(accessToken, OAuthSignature.HEADER);
    }

    @Test
    public void testGetAllBottles() {
        givenAuthenticated()
                .get(API_PATH + "shelf/bottles")
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
                .get(API_PATH + "shelf/bottles/{id}")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("id", Matchers.not(Matchers.empty()));
    }

    @Test
    public void testAddNewBottle() {

        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/shelf/new_bottle_v1.json");
        resourceAsJSON.put("brandName", resourceAsJSON.getString("brandName") + " - " + RandomStringUtils.randomAlphabetic(6));

        String id = givenAuthenticated()
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
                .put(API_PATH + "shelf/bottles")
                .then()
                .statusCode(202)
                .body("id", matchesPattern(RESOURCE_ID));
    }

    @Test
    public void testRemoveBottle() {
        Assert.isTrue(StringUtils.contains(bottleId, "bottle-"), "Bottle Id from the previous step is missing.");

        givenAuthenticated()
                .when()
                .pathParam("id", bottleId)
                .delete(API_PATH + "shelf/bottles/{id}")
                .then()
                .statusCode(204);
    }

}
