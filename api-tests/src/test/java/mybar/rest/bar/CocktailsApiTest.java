package mybar.rest.bar;

import io.restassured.RestAssured;
import io.restassured.authentication.OAuthSignature;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import mybar.JsonUtil;
import mybar.OAuthAuthenticator;
import mybar.spring.ApiTestsContextConfiguration;
import mybar.rest.Um;
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
public class CocktailsApiTest {

    public static final String RESOURCE_ID_PATTERN = "^[a-z]+(-[a-zA-Z0-9]{6}+)*$";
    public static Pattern RESOURCE_ID = Pattern.compile(RESOURCE_ID_PATTERN);

    private final JsonUtil jsonUtil = new JsonUtil();

    @Autowired
    private OAuthAuthenticator authenticator;

    private static String cocktailId;

    private RequestSpecification givenAuthenticated() {

        final String accessToken = authenticator.getAccessToken(Um.TEST_USERNAME, Um.USER_PASS);

        return RestAssured
                .given()
                .auth()
                .oauth2(accessToken, OAuthSignature.HEADER);
    }

    @Test
    public void testGetAllCocktails() {
        givenAuthenticated()
                .get(API_PATH + "cocktails")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("longs", Matchers.hasSize(1));
    }

    @Test
    public void testGetCocktailsByMenu() {
        givenAuthenticated()
                .when()
                .param("filter", "shooters")
                .get(API_PATH + "cocktails")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("shooters", Matchers.hasSize(6));
    }

    @Test
    public void testGetCocktailDetailsById() {
        givenAuthenticated()
                .when()
                .pathParam("id", "cocktail-000001")
                .get(API_PATH + "cocktails/{id}")
                .then()
                .statusCode(200)
                .and()
                .body("id", Matchers.equalTo("cocktail-000001"));
    }

    @Test
    public void testAddNewCocktail() {

        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/cocktails/new_cocktail_v1.json");
        resourceAsJSON.put("name", resourceAsJSON.getString("name") + " - " + RandomStringUtils.randomAlphabetic(6));

        String id = givenAuthenticated()
                .when()
                .contentType(ContentType.JSON)
                .and()
                .body(resourceAsJSON.toString())
                .post(API_PATH + "cocktails")
                .then()
                .statusCode(201)
                .body("id", matchesPattern(RESOURCE_ID))
                .extract()
                .path("id");

        CocktailsApiTest.cocktailId = id;
    }

    @Test
    public void testUpdateExistedCocktail() {

        String resourceAsString = jsonUtil.resourceAsString("/data/cocktails/cocktail_v1.json");

        givenAuthenticated()
                .when()
                .contentType(ContentType.JSON)
                .and()
                .body(resourceAsString)
                .put(API_PATH + "cocktails")
                .then()
                .statusCode(202)
                .body("id", matchesPattern(RESOURCE_ID));
    }

    @Test
    public void testRemoveExistedCocktail() {
        Assert.isTrue(StringUtils.contains(cocktailId, "cocktail-"), "Cocktail Id from the previous step is missing.");

        givenAuthenticated()
                .when()
                .pathParam("id", cocktailId)
                .delete(API_PATH + "cocktails/{id}")
                .then()
                .statusCode(204);
    }

}
