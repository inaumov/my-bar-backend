package mybar.web.rest.bar;

import mybar.service.bar.IngredientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"test-rest-context.xml", "test-security-context.xml"})
public class IngredientsRestControllerAuthTest {
    public static final String USERNAME = "joe";
    public static final String ROLE_USER = "USER";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private IngredientService ingredientService;

    @Before
    public void setUp() {
        Filter springSecurityFilterChain = (Filter) webApplicationContext.getBean("springSecurityFilterChain");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void test_findAll() throws Exception {
        when(ingredientService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/ingredients")
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123")))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isOk());
    }

}