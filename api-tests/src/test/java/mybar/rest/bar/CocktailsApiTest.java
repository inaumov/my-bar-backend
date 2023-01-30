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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

import static mybar.CommonPaths.API_PATH;
import static mybar.rest.Constants.TEST_USERNAME;
import static mybar.rest.Constants.USER_PASS;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

@TestMethodOrder(MethodOrderer.MethodName.class)
class CocktailsApiTest extends ApiTest {

    public static final String RESOURCE_ID_PATTERN = "^[a-z]+(-[a-zA-Z0-9]{6}+)*$";
    public static Pattern RESOURCE_ID = Pattern.compile(RESOURCE_ID_PATTERN);

    @BeforeEach
    void setUp() {
        runSql("sql/cocktail.sql", "sql/ingredient.sql", "sql/cocktail_to_ingredient.sql");
    }

    @Test
    void testGetAllCocktails() {
        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .get(API_PATH + "cocktails")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("shots", Matchers.hasSize(4))
                .body("longs", Matchers.hasSize(7));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "shots, 4",
            "longs, 7"
    })
    void testGetCocktailsByMenu(String param, int size) {
        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .param("filter", param)
                .get(API_PATH + "cocktails")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(param, Matchers.hasSize(size));
    }

    @Test
    void testGetCocktailDetailsById() {
        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .pathParam("id", "cocktail-000001")
                .get(API_PATH + "cocktails/{id}")
                .then()
                .statusCode(200)
                .and()
                .body("id", Matchers.equalTo("cocktail-000001"));
    }

    @Test
    void testAddNewCocktail() {

        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/cocktails/new_cocktail_v1.json");
        resourceAsJSON.put("name", resourceAsJSON.getString("name") + " - " + RandomStringUtils.randomAlphabetic(6));

        String id = givenAuthenticated(TEST_USERNAME, USER_PASS)
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

        Assert.notNull(id, "Id should be created");
    }

    @Test
    void testUpdateExistedCocktail() {

        String resourceAsString = jsonUtil.resourceAsString("/data/cocktails/cocktail_v1.json");

        givenAuthenticated(TEST_USERNAME, USER_PASS)
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
    void testRemoveExistedCocktail() {
        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .pathParam("id", "cocktail-000011")
                .delete(API_PATH + "cocktails/{id}")
                .then()
                .statusCode(204);
    }

}
