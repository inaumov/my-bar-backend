package mybar.rest.bar;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Test;

public class MenuApiTest {

    @Test
    public void testGetMenu() {
        Response res = RestAssured.get("http://localhost:8089/api/bar/menu");
        Assert.assertEquals(200, res.getStatusCode());
        String json = res.asString();
//        JsonPath jp = new JsonPath(json);
    }

}
