package mybar.web.rest.bar;

import mybar.api.bar.ICocktail;
import mybar.dto.bar.CocktailDto;
import mybar.service.bar.CocktailsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"test-rest-context.xml", "test-security-context.xml"})
public class CocktailsRestControllerAuthTest {
    public static final String USERNAME = "joe";
    public static final String ROLE_USER = "USER";

    public static final String TEST_ID_1 = "cocktail-000001";
    public static final String TEST_ID_2 = "cocktail-000002";
    public static final String MENU_NAME = "Chai";
    public static final String NAME = "Rum Cola";
    public static final String DESCRIPTION = "Loren ipsum";
    public static final String IMAGE_URL = "http://cocktail-image.jpg";
    public static final double TEST_VOLUME_VALUE = 25; // TODO

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private CocktailsService cocktailsService;

    @Before
    public void setUp() {
        Filter springSecurityFilterChain = (Filter) webApplicationContext.getBean("springSecurityFilterChain");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        reset(cocktailsService);
    }

    @Test
    public void test_findById() throws Exception {
        when(cocktailsService.findCocktailById(TEST_ID_1)).thenReturn(new CocktailDto());

        mockMvc.perform(get("/cocktails/" + TEST_ID_1)
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123")))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    public void test_findAll() throws Exception {
        when(cocktailsService.getAllCocktails()).thenReturn(Collections.<String, List<ICocktail>>emptyMap());

        mockMvc.perform(get("/cocktails")
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123")))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    public void test_create() throws Exception {
        when(cocktailsService.saveCocktail(Matchers.any(ICocktail.class))).thenReturn(new CocktailDto());

        mockMvc.perform(post("/cocktails")
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isCreated());
    }

    @Test
    public void test_update() throws Exception {
        when(cocktailsService.updateCocktail(Matchers.any(ICocktail.class))).thenReturn(new CocktailDto());

        mockMvc.perform(put("/cocktails")
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_delete() throws Exception {
        doNothing().when(cocktailsService).deleteCocktailById(TEST_ID_1);

        mockMvc.perform(delete("/cocktails/" + TEST_ID_1)
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123")))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isNoContent());
    }

}