package mybar.web.rest.bar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import mybar.api.bar.ICocktail;
import mybar.api.bar.Measurement;
import mybar.api.bar.ingredient.IBeverage;
import mybar.app.RestBeanConverter;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.cocktails.CocktailAvailabilityCalculator;
import mybar.dto.bar.CocktailDto;
import mybar.dto.bar.CocktailToIngredientDto;
import mybar.exception.CocktailNotFoundException;
import mybar.exception.UnknownIngredientsException;
import mybar.exception.UnknownMenuException;
import mybar.service.bar.CocktailsService;
import mybar.web.rest.ARestControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CocktailsController.class)
public class CocktailsRestControllerTest extends ARestControllerTest {

    public static final String TEST_ID_1 = "cocktail-000001";
    public static final String TEST_ID_2 = "cocktail-000002";
    public static final String MENU_NAME = "Chai";
    public static final String NAME = "Rum Cola";
    public static final String DESCRIPTION = "Loren ipsum";
    public static final String IMAGE_URL = "http://cocktail-image.jpg";
    public static final double TEST_VOLUME_VALUE = 25; // TODO

    @MockBean
    private CocktailsService cocktailsServiceMock;
    @MockBean
    private CocktailAvailabilityCalculator availabilityCalculatorMock;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        reset(cocktailsServiceMock);
    }

    @Test
    public void test_findById_notAuthorized() throws Exception {
        String accessToken = "not_a_real_token";

        when(cocktailsServiceMock.findCocktailById(TEST_ID_1)).thenReturn(new CocktailDto());

        mockMvc.perform(get("/cocktails/{0}", TEST_ID_1)
                .header("Authorization", "Bearer " + accessToken)
                .accept(CONTENT_TYPE))

                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void test_findById_wrongRole() throws Exception {

        when(cocktailsServiceMock.findCocktailById(TEST_ID_1)).thenReturn(new CocktailDto());

        mockMvc.perform(makePreAuthorizedRequest(ADMIN, ADMIN, get("/cocktails/{0}", TEST_ID_1)))

                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(status().isForbidden());
    }

    @Test
    public void findById_Should_ReturnCocktailEntry() throws Exception {

        when(cocktailsServiceMock.findCocktailById(TEST_ID_1)).thenReturn(createCocktailDto());

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, get("/cocktails/{0}", TEST_ID_1)))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$.id", is(TEST_ID_1)))
                .andExpect(jsonPath("$.relatedToMenu", is(MENU_NAME)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.imageUrl", is(IMAGE_URL)))
                .andExpect(jsonPath("$.available", is("UNDEFINED")))

                .andExpect(jsonPath("$.ingredients.beverages", hasSize(1)))
                .andExpect(jsonPath("$.ingredients.beverages[0].ingredientId", is(5)))
                .andExpect(jsonPath("$.ingredients.beverages[0].volume", is(TEST_VOLUME_VALUE)))
                .andExpect(jsonPath("$.ingredients.beverages[0].measurement", is(Measurement.ML.name())))
                .andExpect(jsonPath("$.ingredients.beverages[0].missing", is(nullValue())));

        verify(cocktailsServiceMock, times(1)).findCocktailById(anyString());
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void findById_Should_ThrowNotFound() throws Exception {

        when(cocktailsServiceMock.findCocktailById(TEST_ID_2)).thenThrow(new CocktailNotFoundException(TEST_ID_2));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, get("/cocktails/{0}", TEST_ID_2)))

                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"There is no cocktail with id: " + TEST_ID_2)));

        verify(cocktailsServiceMock, times(1)).findCocktailById(anyString());
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void findAll_Should_ReturnEmptyArray() throws Exception {

        when(cocktailsServiceMock.getAllCocktails()).thenReturn(Collections.emptyMap());

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, get("/cocktails")))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$.*", hasSize(0)));

        verify(cocktailsServiceMock, times(1)).getAllCocktails();
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void findAll_Should_ReturnAllCocktailEntries() throws Exception {

        final CocktailDto first = new CocktailDto();
        first.setId("cocktail-000005");

        final CocktailDto second = new CocktailDto();
        second.setId("cocktail-000010");

        ImmutableMap<String, List<ICocktail>> cocktails = ImmutableMap.<String, List<ICocktail>>of(
                "shot", Lists.<ICocktail>newArrayList(first, second),
                "other", Lists.<ICocktail>newArrayList(createCocktailDto())
        );
        when(cocktailsServiceMock.getAllCocktails()).thenReturn(cocktails);

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, get("/cocktails")))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$.shot", hasSize(2)))
                .andExpect(jsonPath("$.shot[0].id", is("cocktail-000005")))
                .andExpect(jsonPath("$.shot[1].id", is("cocktail-000010")))

                .andExpect(jsonPath("$.other", hasSize(1)))
                .andExpect(jsonPath("$.other[0].id", is(TEST_ID_1)))
                .andExpect(jsonPath("$.other[0].name", is(NAME)))
                .andExpect(jsonPath("$.other[0].description").doesNotExist())
                .andExpect(jsonPath("$.other[0].imageUrl", is(IMAGE_URL)))
                .andExpect(jsonPath("$.other[0].available", is("UNDEFINED")))
                .andExpect(jsonPath("$.other[0].relatedToMenu").doesNotExist())

                .andExpect(jsonPath("$.other[0].ingredients.beverages", hasSize(1)))
                .andExpect(jsonPath("$.other[0].ingredients.beverages[0].ingredientId", is(5)))
                .andExpect(jsonPath("$.other[0].ingredients.beverages[0].volume", is(TEST_VOLUME_VALUE)))
                .andExpect(jsonPath("$.other[0].ingredients.beverages[0].measurement", is(Measurement.ML.name())))
                .andExpect(jsonPath("$.other[0].ingredients.beverages[0].missing").doesNotExist());

        verify(cocktailsServiceMock, times(1)).getAllCocktails();
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void findAll_Should_ReturnFilteredCocktailEntries() throws Exception {

        final CocktailDto first = new CocktailDto();
        first.setId("cocktail-000005");

        final CocktailDto second = new CocktailDto();
        second.setId("cocktail-000010");

        when(cocktailsServiceMock.getAllCocktailsForMenu("any")).thenReturn(Lists.<ICocktail>newArrayList(first, second, createCocktailDto()));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER,
                get("/cocktails")
                        .param("filter", "any")))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$.any", hasSize(3)))
                .andExpect(jsonPath("$.any[0].id", is("cocktail-000005")))
                .andExpect(jsonPath("$.any[0].available", is("UNDEFINED")))
                .andExpect(jsonPath("$.any[1].id", is("cocktail-000010")))
                .andExpect(jsonPath("$.any[2].id", is(TEST_ID_1)))
                .andExpect(jsonPath("$.any[2].name", is(NAME)))
                .andExpect(jsonPath("$.any[2].description").doesNotExist())
                .andExpect(jsonPath("$.any[2].imageUrl", is(IMAGE_URL)))
                .andExpect(jsonPath("$.any[2].available", is("UNDEFINED")))
                .andExpect(jsonPath("$.any[2].relatedToMenu").doesNotExist())

                .andExpect(jsonPath("$.any[2].ingredients.beverages", hasSize(1)))
                .andExpect(jsonPath("$.any[2].ingredients.beverages[0].ingredientId", is(5)))
                .andExpect(jsonPath("$.any[2].ingredients.beverages[0].volume", is(TEST_VOLUME_VALUE)))
                .andExpect(jsonPath("$.any[2].ingredients.beverages[0].measurement", is(Measurement.ML.name())))
                .andExpect(jsonPath("$.any[2].ingredients.beverages[0].missing").doesNotExist());

        verify(cocktailsServiceMock, times(1)).getAllCocktailsForMenu("any");
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void create_Should_CreateNewCocktail() throws Exception {
        CocktailDto cocktailDto = createCocktailDto();

        when(cocktailsServiceMock.saveCocktail(Mockito.any(ICocktail.class))).thenReturn(cocktailDto);
        String requestJson = toRequestJson(RestBeanConverter.toCocktailBean(cocktailDto));

        ResultActions resultActions = mockMvc.perform(makePreAuthorizedRequest(USER, USER, post("/cocktails"))
                .content(requestJson))

                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(CONTENT_TYPE));

        assertCocktailResponseBody(resultActions);

        verify(cocktailsServiceMock, times(1)).saveCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void create_Should_ThrowMenuUnknown() throws Exception {

        when(cocktailsServiceMock.saveCocktail(Mockito.any(ICocktail.class))).thenThrow(new UnknownMenuException("unknown"));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, post("/cocktails"))
                .content("{}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Menu name [unknown] is unknown.")));

        verify(cocktailsServiceMock, times(1)).saveCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void create_Should_ValidateCocktailNameRequired() throws Exception {

        when(cocktailsServiceMock.saveCocktail(Mockito.any(ICocktail.class))).thenCallRealMethod();

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, post("/cocktails"))
                .content("{\"relatedToMenu\":\"test\"}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Cocktail name is required.")));

        verify(cocktailsServiceMock, times(1)).saveCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void create_Should_ValidateMenuNameRequired() throws Exception {

        when(cocktailsServiceMock.saveCocktail(Mockito.any(ICocktail.class))).thenCallRealMethod();

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, post("/cocktails"))
                .content("{\"name\":\"test\"}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Menu name is required.")));

        verify(cocktailsServiceMock, times(1)).saveCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void create_Should_ValidateIngredientsUnknown() throws Exception {

        when(cocktailsServiceMock.saveCocktail(Mockito.any(ICocktail.class))).thenThrow(new UnknownIngredientsException(Lists.newArrayList(15, 20, 40)));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, post("/cocktails"))
                .content("{}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Provided ingredients [15, 20, 40] are unknown.")));

        verify(cocktailsServiceMock, times(1)).saveCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void update_Should_UpdateCocktail() throws Exception {
        final CocktailDto cocktailDto = createCocktailDto();

        when(cocktailsServiceMock.updateCocktail(Mockito.any(ICocktail.class))).thenReturn(cocktailDto);

        String requestJson = toRequestJson(RestBeanConverter.toCocktailBean(cocktailDto));

        ResultActions resultActions = mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/cocktails"))
                .content(requestJson))

                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(CONTENT_TYPE));

        assertCocktailResponseBody(resultActions);

        verify(cocktailsServiceMock, times(1)).updateCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    private void assertCocktailResponseBody(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(jsonPath("$.id", is(TEST_ID_1)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.imageUrl", is(IMAGE_URL)))
                .andExpect(jsonPath("$.relatedToMenu", is(MENU_NAME)))
                .andExpect(jsonPath("$.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.available", is("UNDEFINED")))

                .andExpect(jsonPath("$.ingredients.beverages", hasSize(1)))
                .andExpect(jsonPath("$.ingredients.beverages[0].ingredientId", is(5)))
                .andExpect(jsonPath("$.ingredients.beverages[0].volume", is(TEST_VOLUME_VALUE)))
                .andExpect(jsonPath("$.ingredients.beverages[0].measurement", is(Measurement.ML.name())))
                .andExpect(jsonPath("$.ingredients.beverages[0].missing").doesNotExist());
    }

    @Test
    public void update_Should_ThrowNotFound() throws Exception {

        when(cocktailsServiceMock.updateCocktail(Mockito.any(ICocktail.class))).thenThrow(new CocktailNotFoundException(TEST_ID_2));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/cocktails"))
                .content("{}"))

                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"There is no cocktail with id: " + TEST_ID_2)));

        verify(cocktailsServiceMock, times(1)).updateCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void update_Should_ThrowMenuUnknown() throws Exception {
        when(cocktailsServiceMock.updateCocktail(Mockito.any(ICocktail.class))).thenThrow(new UnknownMenuException("unknown"));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/cocktails"))
                .content("{}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Menu name [unknown] is unknown.")));

        verify(cocktailsServiceMock, times(1)).updateCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void update_Should_ValidateCocktailNameRequired() throws Exception {

        when(cocktailsServiceMock.updateCocktail(Mockito.any(ICocktail.class))).thenCallRealMethod();

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/cocktails"))

                .content("{\"id\":\"cocktail-test00\",\"relatedToMenu\":\"test\"}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Cocktail name is required.")));

        verify(cocktailsServiceMock, times(1)).updateCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void update_Should_ValidateMenuNameRequired() throws Exception {

        when(cocktailsServiceMock.updateCocktail(Mockito.any(ICocktail.class))).thenCallRealMethod();

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/cocktails"))
                .content("{\"id\":\"cocktail-test00\",\"name\":\"test\"}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Menu name is required.")));

        verify(cocktailsServiceMock, times(1)).updateCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void update_Should_ValidateIngredientsUnknown() throws Exception {

        when(cocktailsServiceMock.updateCocktail(Mockito.any(ICocktail.class))).thenThrow(new UnknownIngredientsException(Lists.newArrayList(15, 20, 40)));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/cocktails"))
                .content("{}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Provided ingredients [15, 20, 40] are unknown.")));

        verify(cocktailsServiceMock, times(1)).updateCocktail(Mockito.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void delete_Should_DeleteCocktail() throws Exception {

        doNothing().when(cocktailsServiceMock).deleteCocktailById(TEST_ID_1);

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, delete("/cocktails/{0}", TEST_ID_1)))

                .andDo(print())
                .andExpect(status().isNoContent());

        verify(cocktailsServiceMock, times(1)).deleteCocktailById(TEST_ID_1);
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    @Test
    public void delete_Should_ThrowNotFound() throws Exception {

        doThrow(new CocktailNotFoundException(TEST_ID_2)).when(cocktailsServiceMock).deleteCocktailById(TEST_ID_2);

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, delete("/cocktails/{0}", TEST_ID_2)))

                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"There is no cocktail with id: " + TEST_ID_2)));

        verify(cocktailsServiceMock, times(1)).deleteCocktailById(TEST_ID_2);
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

    private String toRequestJson(CocktailBean testCocktail) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(testCocktail);
    }

    public static CocktailDto createCocktailDto() {
        final CocktailDto cocktailDto = new CocktailDto();
        cocktailDto.setId(TEST_ID_1);
        cocktailDto.setName(NAME);
        cocktailDto.setDescription(DESCRIPTION);
        cocktailDto.setImageUrl(IMAGE_URL);
        cocktailDto.setMenuName(MENU_NAME);

        CocktailToIngredientDto beverage = new CocktailToIngredientDto();
        beverage.setIngredientId(5);
        beverage.setVolume(25);
        beverage.setMeasurement(Measurement.ML);

        cocktailDto.setIngredients(ImmutableMap.<String, Collection<CocktailToIngredientDto>>of(IBeverage.GROUP_NAME, Collections.singleton(beverage)));
        return cocktailDto;
    }

}