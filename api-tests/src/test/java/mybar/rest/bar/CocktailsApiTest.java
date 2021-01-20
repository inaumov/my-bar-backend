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

public class CocktailsApiTest {

    public static final String RESOURCE_ID_PATTERN = "^[a-z]+(-[a-zA-Z0-9]{6}+)*$";
    public static Pattern RESOURCE_ID = Pattern.compile(RESOURCE_ID_PATTERN);

    private final JsonUtil jsonUtil = new JsonUtil();

    private static String cocktailId;

    private static RequestSpecification givenAuthenticated() {
        return RestAssured
                .given()
                .auth()
                .preemptive()
                .basic(Um.TEST_USERNAME, Um.USER_PASS);
    }

    @Test
    public void testGetAllCocktails() {
        givenAuthenticated()
                .get("http://localhost:8089/api/bar/cocktails")
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
                .get("http://localhost:8089/api/bar/cocktails")
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
                .get("http://localhost:8089/api/bar/cocktails/{id}")
                .then()
                .statusCode(200)
                .and()
                .body("id", Matchers.equalTo("cocktail-000001"));
    }

    @Test
    public void testAddNewCocktail() {

        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/cocktails/new_cocktail_v1.json");
        resourceAsJSON.put("name", resourceAsJSON.get("name") + " - " + RandomStringUtils.randomAlphabetic(6));

        String id = givenAuthenticated()
                .when()
                .contentType(ContentType.JSON)
                .and()
                .body(resourceAsJSON.toString())
                .post("http://localhost:8089/api/bar/cocktails")
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
                .put("http://localhost:8089/api/bar/cocktails")
                .then()
                .statusCode(202)
                .body("id", matchesPattern(RESOURCE_ID));
    }

    @Test
    public void testRemoveExistedCocktail() {
        givenAuthenticated()
                .when()
                .pathParam("id", cocktailId)
                .delete("http://localhost:8089/api/bar/cocktails/{id}")
                .then()
                .statusCode(204);
    }

}
