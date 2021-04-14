package mybar.rest.actuator;

import io.restassured.RestAssured;
import io.restassured.authentication.OAuthSignature;
import io.restassured.specification.RequestSpecification;
import mybar.OAuthAuthenticator;
import mybar.rest.Um;
import mybar.spring.ApiTestsContextConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApiTestsContextConfiguration.class}, loader = AnnotationConfigContextLoader.class)
public class ActuatorApiTest {

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
    public void testActuatorUpAndRunning() {
        givenAuthenticated()
                .get("http://localhost:8089/api/bar/actuator")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("_links", Matchers.hasKey("health"))
                .body("_links", Matchers.hasKey("info"))
                .body("_links", Matchers.hasKey("metrics"));

    }

}
