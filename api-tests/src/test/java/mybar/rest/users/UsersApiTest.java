package mybar.rest.users;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import mybar.rest.ApiTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static mybar.CommonPaths.API_PATH;
import static mybar.rest.Constants.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class UsersApiTest extends ApiTest {

    @BeforeEach
    void setUp() {
        runSql("/um/insert_um_test_data.sql");
    }

    @Test
    void test_register_new_user_no_auth_required() {
        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/users/new_user_v1.json");
        String idPrefix = RandomStringUtils.randomNumeric(3);
        resourceAsJSON.put("username", resourceAsJSON.getString("username") + "_" + idPrefix);
        resourceAsJSON.put("email", resourceAsJSON.getString("email").replaceFirst("@", "_" + idPrefix + "@"));

        RestAssured
                .given()
                .body(resourceAsJSON.toString())
                .when()
                .contentType(ContentType.JSON)
                .post(API_PATH + "users/register")
                .then()
                .assertThat()
                .statusCode(201);
    }

    @Test
    void test_register_user_occupied() {
        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/users/new_user_v1.json");

        RestAssured
                .given()
                .body(resourceAsJSON.toString())
                .when()
                .contentType(ContentType.JSON)
                .post(API_PATH + "users/register")
                .then()
                .assertThat()
                .statusCode(403)
                .body("errorMessage", Matchers.equalTo("Username [pvl_zbrv] has been already occupied"));
    }

    @Test
    void test_register_user_email_occupied() {
        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/users/new_user_v1_email_occupied.json");

        RestAssured
                .given()
                .body(resourceAsJSON.toString())
                .when()
                .contentType(ContentType.JSON)
                .post(API_PATH + "users/register")
                .then()
                .assertThat()
                .statusCode(403)
                .body("errorMessage", Matchers.equalTo("There is an account with that email: pavluxa@gmail.com"));
    }

    @Test
    void test_get_all_users_as_admin() {
        givenAuthenticated(ADMIN, ADMIN_PASS)
                .get(API_PATH + "users")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("size()", Matchers.greaterThan(1));
    }

    @Test
    void test_change_password() {
        givenAuthenticated(TEST_USERNAME, USER_PASS)
                .given()
                .body("{\"newPassword\":\"user\"})")
                .when()
                .contentType(ContentType.JSON)
                .put(API_PATH + "users/{0}/changePassword", TEST_USERNAME)
                .then()
                .assertThat()
                .statusCode(202);
    }

}
