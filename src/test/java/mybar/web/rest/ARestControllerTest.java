package mybar.web.rest;

import mybar.web.config.auth.AuthenticationManagerConfiguration;
import mybar.web.config.auth.AuthorizationServerConfiguration;
import mybar.web.config.auth.ResourceServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import({AuthenticationManagerConfiguration.class, AuthorizationServerConfiguration.class, TestConfig.class, ResourceServerConfiguration.class})
public abstract class ARestControllerTest {

    public static final String USER = "user";
    public static final String ADMIN = "admin";

    @Autowired
    protected MockMvc mockMvc;

    protected String obtainAccessToken(String username, String password) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "api-tests");
        params.add("username", username);
        params.add("password", password);

        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic("api-tests", "bGl2ZS10ZXN0"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JsonParser jsonParser = new Jackson2JsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    protected MockHttpServletRequestBuilder makePreAuthorizedRequest(String user, String password, MockHttpServletRequestBuilder mockHttpServletRequestBuilder) throws Exception {
        String accessToken = obtainAccessToken(user, password);
        return mockHttpServletRequestBuilder
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON);
    }

}
