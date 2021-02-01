package mybar.rest.bar;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;

public class MenuApiTest {

    @Test
    public void testGetMenu_no_auth_required() {
        RestAssured.
                get("http://localhost:8089/api/bar/menu")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("name", Matchers.hasSize(5));
    }

}
