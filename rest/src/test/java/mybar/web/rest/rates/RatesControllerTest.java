package mybar.web.rest.rates;

import mybar.dto.RateDto;
import mybar.exception.CocktailNotFoundException;
import mybar.service.rates.RatesEventService;
import mybar.service.rates.RatesService;
import mybar.web.rest.ARestControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(RatesController.class)
public class RatesControllerTest extends ARestControllerTest {

    public static final String COCKTAIL_ID = "cocktail-000099";
    public static final int STARS = 7;
    public static final String basePath = "/v1/rates";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatesService ratesServiceMock;

    @MockBean
    private RatesEventService ratesEventServiceMock;

    @BeforeEach
    public void setup() {
    }

    @AfterEach
    public void tearDown() {
        reset(ratesServiceMock);
    }

    @Test
    public void test_rateCocktail_create() throws Exception {

        RateDto resultDto = new RateDto();
        resultDto.setCocktailId(COCKTAIL_ID);
        resultDto.setStars(STARS);
        resultDto.setRatedAt(LocalDateTime.now());

        when(ratesEventServiceMock.rateCocktail(eq(USER), eq(COCKTAIL_ID), eq(STARS))).thenReturn(resultDto);

        // rate cocktail
        MockHttpServletRequestBuilder requestBuilder =
                makePreAuthorizedRequest(USER, USER, MockMvcRequestBuilders.post(basePath))
                        .content(createRateInJson(COCKTAIL_ID, STARS));

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated());

        verify(ratesEventServiceMock, times(1)).rateCocktail(eq(USER), eq(COCKTAIL_ID), eq(STARS));
        verifyNoMoreInteractions(ratesEventServiceMock);
    }

    private static String createRateInJson(String cocktailId, int stars) {
        return "{\"cocktailId\":\"" + cocktailId + "\",\"stars\":\"" + stars + "\"}";
    }

    @Test
    public void test_updateRate() throws Exception {

        RateDto resultDto = new RateDto();
        resultDto.setCocktailId(COCKTAIL_ID);
        resultDto.setStars(STARS);
        resultDto.setRatedAt(LocalDateTime.now());

        when(ratesEventServiceMock.rateCocktail(eq(USER), eq(COCKTAIL_ID), eq(STARS))).thenReturn(resultDto);

        // rate cocktail
        MockHttpServletRequestBuilder requestBuilder =
                makePreAuthorizedRequest(USER, USER, MockMvcRequestBuilders.put(basePath))
                        .content(createRateInJson(COCKTAIL_ID, STARS));

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk());

        verify(ratesEventServiceMock, times(1)).rateCocktail(eq(USER), eq(COCKTAIL_ID), eq(STARS));
        verifyNoMoreInteractions(ratesEventServiceMock);
    }

    @Test
    public void test_removeFromRates() throws Exception {
        doNothing().when(ratesServiceMock).removeCocktailFromRates(eq(USER), eq(COCKTAIL_ID));

        MockHttpServletRequestBuilder requestBuilder = makePreAuthorizedRequest(USER, USER,
                MockMvcRequestBuilders.delete(basePath + "/{0}", COCKTAIL_ID));

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isNoContent())
                .andDo(MockMvcResultHandlers.print());

        verify(ratesServiceMock, atLeastOnce()).removeCocktailFromRates(eq(USER), eq(COCKTAIL_ID));
    }

    @Test
    public void test_getRatedCocktails() throws Exception {

        RateDto resultDto = new RateDto();
        resultDto.setCocktailId(COCKTAIL_ID);
        resultDto.setStars(STARS);
        resultDto.setRatedAt(LocalDateTime.now());

        when(ratesServiceMock.getRatedCocktails(eq(USER))).thenReturn(Collections.singletonList(resultDto));

        MockHttpServletRequestBuilder requestBuilder =
                makePreAuthorizedRequest(USER, USER, MockMvcRequestBuilders
                        .get(basePath + "/ratedCocktails"));

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_rateCocktail_when_unknown() throws Exception {
        when(ratesEventServiceMock.rateCocktail(anyString(), eq("unknown"), anyInt())).thenThrow(new CocktailNotFoundException("unknown"));

        // rate cocktail
        MockHttpServletRequestBuilder requestBuilder = makePreAuthorizedRequest(USER, USER,
                MockMvcRequestBuilders.post(basePath))
                        .content(createRateInJson("unknown", 1));

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("errorMessage\":\"Could not rate cocktail: " + "unknown")))
                .andDo(MockMvcResultHandlers.print());

        verify(ratesEventServiceMock, times(1)).rateCocktail(anyString(), eq("unknown"), anyInt());
        verifyNoMoreInteractions(ratesEventServiceMock);
    }

    @Test
    public void test_rateCocktail_when_bad_request() throws Exception {
        when(ratesEventServiceMock.rateCocktail(anyString(), eq(COCKTAIL_ID), eq(null))).thenCallRealMethod();

        // rate cocktail
        MockHttpServletRequestBuilder requestBuilder = makePreAuthorizedRequest(USER, USER,
                MockMvcRequestBuilders.post(basePath))
                        .content("{\"cocktailId\":\"" + COCKTAIL_ID + "\"}");

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("errorMessage\":\"Stars number should be from 1 to 10.")))
                .andDo(MockMvcResultHandlers.print());

        verify(ratesEventServiceMock, times(1)).rateCocktail(anyString(), eq(COCKTAIL_ID), eq(null));
        verifyNoMoreInteractions(ratesEventServiceMock);
    }

}