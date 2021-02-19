package mybar.rest.users;

import io.restassured.RestAssured;
import io.restassured.authentication.OAuthSignature;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import mybar.JsonUtil;
import mybar.OAuthAuthenticator;
import mybar.rest.Um;
import mybar.spring.ApiTestsContextConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApiTestsContextConfiguration.class}, loader = AnnotationConfigContextLoader.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsersApiTest {

    private final JsonUtil jsonUtil = new JsonUtil();

    @Autowired
    private OAuthAuthenticator authenticator;

    @Test
    public void test_register_new_user_no_auth_required() {
        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/users/new_user_v1.json");
        String idPrefix = RandomStringUtils.randomNumeric(3);
        resourceAsJSON.put("username", resourceAsJSON.getString("username") + "_" + idPrefix);
        resourceAsJSON.put("email", resourceAsJSON.getString("email").replaceFirst("@", "_" + idPrefix + "@"));

        RestAssured
                .given()
                .body(resourceAsJSON.toString())
                .when()
                .contentType(ContentType.JSON)
                .post("http://localhost:8089/api/bar/users/register")
                .then()
                .assertThat()
                .statusCode(201);
    }

    @Test
    public void test_register_user_occupied() {
        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/users/new_user_v1.json");

        RestAssured
                .given()
                .body(resourceAsJSON.toString())
                .when()
                .contentType(ContentType.JSON)
                .post("http://localhost:8089/api/bar/users/register")
                .then()
                .assertThat()
                .statusCode(403)
                .body("errorMessage", Matchers.equalTo("Username [pvl_zbrv1] has been already occupied."));
    }

    @Test
    public void test_register_user_email_occupied() {
        JSONObject resourceAsJSON = jsonUtil.resourceAsJSON("/data/users/new_user_v1_email_occupied.json");

        RestAssured
                .given()
                .body(resourceAsJSON.toString())
                .when()
                .contentType(ContentType.JSON)
                .post("http://localhost:8089/api/bar/users/register")
                .then()
                .assertThat()
                .statusCode(403)
                .body("errorMessage", Matchers.equalTo("There is an account with that email: pavluxa@gmail.com ."));
    }

    @Test
    public void test_get_all_users_as_admin() {
        givenAuthenticatedAsAdmin()
                .get("http://localhost:8089/api/bar/users")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("size()", Matchers.greaterThan(1));
    }

    @Test
    public void test_change_password() {
        givenAuthenticated()
                .given()
                .body("{\"newPassword\":\"user\"})")
                .when()
                .contentType(ContentType.JSON)
                .put("http://localhost:8089/api/bar/users/{0}/changePassword", Um.TEST_USERNAME)
                .then()
                .assertThat()
                .statusCode(202);
    }

    private RequestSpecification givenAuthenticatedAsAdmin() {

        final String accessToken = authenticator.getAccessToken(Um.ADMIN, Um.ADMIN_PASS);

        return RestAssured
                .given()
                .auth()
                .oauth2(accessToken, OAuthSignature.HEADER);
    }

    private RequestSpecification givenAuthenticated() {

        final String accessToken = authenticator.getAccessToken(Um.TEST_USERNAME, Um.USER_PASS);

        return RestAssured
                .given()
                .auth()
                .oauth2(accessToken, OAuthSignature.HEADER);
    }

}
