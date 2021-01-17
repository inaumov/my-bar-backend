package mybar.web.rest.rates;

import mybar.dto.RateDto;
import mybar.exception.CocktailNotFoundException;
import mybar.service.rates.RatesService;
import mybar.web.rest.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"test-rates-rest-context.xml", "test-security-context.xml"})
public class RatesControllerTest {

    public static final String USERNAME = "bill";
    public static final String COCKTAIL_ID = "cocktail-000099";
    public static final int STARS = 7;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private RatesService ratesService;

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
        reset(ratesService);
    }

    @Test
    public void test_rateCocktail_create() throws Exception {
        RateDto resultDto = new RateDto();
        resultDto.setCocktailId(COCKTAIL_ID);
        resultDto.setStars(STARS);
        resultDto.setRatedAt(new Date());
        when(ratesService.rateCocktail(eq(USERNAME), eq(COCKTAIL_ID), eq(STARS))).thenReturn(resultDto);

        // rate cocktail
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/rates")
                        .with(user(USERNAME).password("abc123").roles("USER"))
                        .with(httpBasic(USERNAME, "abc123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRateInJson(COCKTAIL_ID, STARS));

        this.mockMvc.perform(builder)
                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated());

        verify(ratesService, times(1)).rateCocktail(eq(USERNAME), eq(COCKTAIL_ID), eq(STARS));
        verifyNoMoreInteractions(ratesService);
    }

    private static String createRateInJson(String cocktailId, int stars) {
        return "{\"cocktailId\":\"" + cocktailId + "\",\"stars\":\"" + stars + "\"}";
    }

    @Test
    public void test_updateRate() throws Exception {
        RateDto resultDto = new RateDto();
        resultDto.setCocktailId(COCKTAIL_ID);
        resultDto.setStars(STARS);
        resultDto.setRatedAt(new Date());
        when(ratesService.rateCocktail(eq(USERNAME), eq(COCKTAIL_ID), eq(STARS))).thenReturn(resultDto);

        // rate cocktail
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/rates")
                        .with(user(USERNAME).password("abc123").roles("USER"))
                        .with(httpBasic(USERNAME, "abc123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRateInJson(COCKTAIL_ID, STARS));

        this.mockMvc.perform(builder)
                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk());

        verify(ratesService, times(1)).rateCocktail(eq(USERNAME), eq(COCKTAIL_ID), eq(STARS));
        verifyNoMoreInteractions(ratesService);
    }

    @Test
    public void test_removeFromRates() throws Exception {
        doNothing().when(ratesService).removeCocktailFromRates(eq(USERNAME), eq(COCKTAIL_ID));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/rates/" + COCKTAIL_ID)
                .with(user(USERNAME).password("abc123").roles("USER"))
                .with(httpBasic(USERNAME, "abc123"));

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isNoContent())
                .andExpect(authenticated().withUsername(USERNAME))
                .andDo(MockMvcResultHandlers.print());

        verify(ratesService, atLeastOnce()).removeCocktailFromRates(eq(USERNAME), eq(COCKTAIL_ID));
    }

    @Test
    public void test_getRatedCocktails() throws Exception {
        RateDto resultDto = new RateDto();
        resultDto.setCocktailId(COCKTAIL_ID);
        resultDto.setStars(STARS);
        resultDto.setRatedAt(new Date());

        when(ratesService.getRatedCocktails(eq(USERNAME))).thenReturn(Collections.singletonList(resultDto));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rates/ratedCocktails")
                .with(user(USERNAME).password("abc123").roles("USER"))
                .with(httpBasic(USERNAME, "abc123"));

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(authenticated().withUsername(USERNAME))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_rateCocktail_when_unknown() throws Exception {
        when(ratesService.rateCocktail(anyString(), eq("unknown"), anyInt())).thenThrow(new CocktailNotFoundException("unknown"));

        // rate cocktail
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/rates")
                        .with(user(USERNAME).password("abc123").roles("USER"))
                        .with(httpBasic(USERNAME, "abc123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRateInJson("unknown", 1));

        this.mockMvc.perform(builder)
                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(MockMvcResultMatchers.status()
                        .isForbidden())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"Could not rate cocktail: " + "unknown")))
                .andDo(MockMvcResultHandlers.print());

        verify(ratesService, times(1)).rateCocktail(anyString(), eq("unknown"), anyInt());
        verifyNoMoreInteractions(ratesService);
    }

    @Test
    public void test_rateCocktail_when_bad_request() throws Exception {
        when(ratesService.rateCocktail(anyString(), eq(COCKTAIL_ID), eq(null))).thenCallRealMethod();

        // rate cocktail
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/rates")
                        .with(user(USERNAME).password("abc123").roles("USER"))
                        .with(httpBasic(USERNAME, "abc123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cocktailId\":\"" + COCKTAIL_ID + "\"}");

        this.mockMvc.perform(builder)
                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"Stars number should be from 1 to 10.")))
                .andDo(MockMvcResultHandlers.print());

        verify(ratesService, times(1)).rateCocktail(anyString(), eq(COCKTAIL_ID), eq(null));
        verifyNoMoreInteractions(ratesService);
    }

}