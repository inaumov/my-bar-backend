package mybar.menu;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.get;

public class MenuTest {

    @Test
    public void testGetMenu() {
        Response res = get("http://localhost:8089/api/bar/menu");
        Assert.assertEquals(200, res.getStatusCode());
        String json = res.asString();
        JsonPath jp = new JsonPath(json);
    }

}
