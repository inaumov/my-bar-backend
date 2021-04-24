package mybar.rest.bar;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static mybar.CommonPaths.API_PATH;

public class MenuApiTest {

    @Test
    public void testGetMenu_no_auth_required() {
        RestAssured.
                get(API_PATH + "menu")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("name", Matchers.hasSize(5));
    }

}
