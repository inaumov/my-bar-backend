package mybar.web.rest.rates;

import mybar.History;
import mybar.service.rates.history.HistoryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"test-rates-rest-context.xml", "test-security-context.xml"})
public class HistoryControllerTest {

    public static final String USERNAME = "bill";
    public static final String COCKTAIL_NAME = "B52";
    public static final int STARS = 7;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private HistoryService historyService;

    @Before
    public void setup() {
        Filter springSecurityFilterChain = (Filter) webApplicationContext.getBean("springSecurityFilterChain");

        DefaultMockMvcBuilder builder = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext);
        this.mockMvc = builder
                .addFilters(springSecurityFilterChain)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        reset(historyService);
    }

    @Test
    public void test_getRatedCocktails() throws Exception {
        History history = new History();
        history.setName(COCKTAIL_NAME);
        history.setStars(STARS);
        history.setUsername(USERNAME);

        when(historyService.getHistoryForPeriod(any(Date.class), any(Date.class))).thenReturn(Collections.singletonList(history));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rates/history")
                .with(user(USERNAME).password("abc123").roles("USER"))
                .with(httpBasic(USERNAME, "abc123"));

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(authenticated().withUsername(USERNAME))
                .andDo(MockMvcResultHandlers.print())

                .andExpect(jsonPath("$[0].stars", is(STARS)))
                .andExpect(jsonPath("$[0].name", is(COCKTAIL_NAME)))
                .andExpect(jsonPath("$[0].username", is(USERNAME)));
    }

}