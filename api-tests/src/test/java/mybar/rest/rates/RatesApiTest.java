package mybar.rest.rates;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mybar.rest.ApiTest;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static mybar.CommonPaths.API_PATH;
import static mybar.rest.Constants.TEST_USERNAME;
import static mybar.rest.Constants.USER_PASS;

@TestMethodOrder(MethodOrderer.MethodName.class)
class RatesApiTest extends ApiTest {

    public static final String RESOURCE_ID_PATTERN = "^[a-z]+(-[a-zA-Z0-9]{6}+)*$";
    public static Pattern RESOURCE_ID = Pattern.compile(RESOURCE_ID_PATTERN);

    @BeforeEach
    void setUp() {
        runSql("sql/rates/rates.sql");
    }

    @Test
    void rates_average() {
        Response response = givenAuthenticated(TEST_USERNAME, USER_PASS)
                .get(API_PATH + "rates/average");
        response
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("size()", Matchers.is(6))
                .log();
        System.out.println(response.prettyPrint());
    }

    @Test
    void my_rated_cocktails() {
        Response response = givenAuthenticated(TEST_USERNAME, USER_PASS)
                .get(API_PATH + "rates/ratedCocktails");
        response
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("size()", Matchers.is(2));
        System.out.println(response.print());
    }

    @Test
    void rate_cocktail() {

        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/rates/new_rate.json");

        LocalDateTime now = LocalDateTime.now();
        String ratedAt = givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .contentType(ContentType.JSON)
                .and()
                .body(resourceAsJSON.toString())
                .post(API_PATH + "rates")
                .then()
                .statusCode(201)
                .body("ratedAt", Matchers.notNullValue())
                .and().body("stars", Matchers.is(10))
                .extract()
                .jsonPath()
                .getString("ratedAt");

        Assert.isTrue(LocalDateTime.parse(ratedAt).isAfter(now), "DateTime should not be earlier then request time");
    }

    @Test
    void update_my_rate() {

        String resourceAsString = jsonUtil.resourceAsString("/data/rates/update_rate.json");

        LocalDateTime now = LocalDateTime.now();
        String ratedAt = givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .contentType(ContentType.JSON)
                .and()
                .body(resourceAsString)
                .put(API_PATH + "rates")
                .then()
                .statusCode(200)
                .body("ratedAt", Matchers.notNullValue())
                .and().body("stars", Matchers.is(10))
                .extract()
                .jsonPath()
                .getString("ratedAt");

        Assert.isTrue(LocalDateTime.parse(ratedAt).isAfter(now), "DateTime should not be earlier then request time");
    }

    @Test
    void remove() {
        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .when()
                .pathParam("id", "cocktail-000010")
                .delete(API_PATH + "rates/{id}")
                .then()
                .statusCode(204);
    }

}
