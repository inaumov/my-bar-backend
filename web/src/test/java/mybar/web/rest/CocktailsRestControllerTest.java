package mybar.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import mybar.UnitsValue;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IMenu;
import mybar.api.bar.ingredient.IBeverage;
import mybar.app.RestBeanConverter;
import mybar.app.bean.bar.CocktailBean;
import mybar.dto.bar.CocktailDto;
import mybar.dto.bar.CocktailToIngredientDto;
import mybar.dto.bar.MenuDto;
import mybar.exception.CocktailNotFoundException;
import mybar.exception.UnknownMenuException;
import mybar.service.bar.CocktailsService;
import mybar.web.rest.bar.AvailableCocktailsWrapper;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-rest-context.xml")
public class CocktailsRestControllerTest {

    public static final int TEST_ID_1 = 1;
    public static final int TEST_ID_2 = 2;
    public static final String MENU_NAME = "TEST_MENU";
    public static final String NAME = "Rum Cola";
    public static final String DESCRIPTION = "Loren ipsum";
    public static final String IMAGE_URL = "http://cocktail-image.jpg";
    public static final double TEST_VOLUME_VALUE = 25; // TODO

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private CocktailsService cocktailsService;
    @Autowired
    private AvailableCocktailsWrapper cocktailsWrapper;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
        reset(cocktailsService);
    }

    @Test
    public void listAllMenuItems_Should_ReturnAllMenuEntries() throws Exception {

        final MenuDto first = new MenuDto();
        first.setId(TEST_ID_1);
        first.setName("shot");

        final MenuDto second = new MenuDto();
        second.setId(TEST_ID_2);
        second.setName("long");

        when(cocktailsService.getAllMenuItems()).thenReturn(Lists.<IMenu>newArrayList(first, second));

        mockMvc.perform(get("/cocktails/menu"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$[0].name", is("shot")))
                .andExpect(jsonPath("$[0].translation", equalTo("Test Shot")))
                .andExpect(jsonPath("$[1].name", is("long")))
                .andExpect(jsonPath("$[1].translation", equalTo("Test Long")));

        verify(cocktailsService, times(1)).getAllMenuItems();
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void findById_Should_ReturnCocktailEntry() throws Exception {

        when(cocktailsService.findCocktailById(TEST_ID_1)).thenReturn(createCocktailDto());

        mockMvc.perform(get("/cocktails/" + TEST_ID_1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.id", is(TEST_ID_1)))
                .andExpect(jsonPath("$.relatedToMenu", is(MENU_NAME)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.imageUrl", is(IMAGE_URL)))
                .andExpect(jsonPath("$.available", is("UNDEFINED")))

                .andExpect(jsonPath("$.ingredients.beverages", hasSize(1)))
                .andExpect(jsonPath("$.ingredients.beverages[0].ingredientId", is(5)))
                .andExpect(jsonPath("$.ingredients.beverages[0].volume", is(TEST_VOLUME_VALUE)))
                .andExpect(jsonPath("$.ingredients.beverages[0].unitsValue", is(UnitsValue.ML.name())))
                .andExpect(jsonPath("$.ingredients.beverages[0].missing", is(nullValue())));

        verify(cocktailsService, times(1)).findCocktailById(anyInt());
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void findById_Should_ThrowNotFound() throws Exception {
        when(cocktailsService.findCocktailById(TEST_ID_2)).thenThrow(new CocktailNotFoundException(TEST_ID_2));

        mockMvc.perform(get("/cocktails/" + TEST_ID_2))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"There is no cocktail with id: " + TEST_ID_2)));

        verify(cocktailsService, times(1)).findCocktailById(anyInt());
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void findAll_Should_ReturnEmptyArray() throws Exception {

        when(cocktailsService.getAllCocktails()).thenReturn(Collections.<String, List<ICocktail>>emptyMap());

        mockMvc.perform(get("/cocktails"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.*", hasSize(0)));

        verify(cocktailsService, times(1)).getAllCocktails();
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void findAll_Should_ReturnAllCocktailEntries() throws Exception {

        final CocktailDto first = new CocktailDto();
        first.setId(5);

        final CocktailDto second = new CocktailDto();
        second.setId(10);

        ImmutableMap<String, List<ICocktail>> cocktails = ImmutableMap.<String, List<ICocktail>>of(
                "shot", Lists.<ICocktail>newArrayList(first, second),
                "other", Lists.<ICocktail>newArrayList(createCocktailDto())
        );
        when(cocktailsService.getAllCocktails()).thenReturn(cocktails);

        mockMvc.perform(get("/cocktails"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.shot.", hasSize(2)))
                .andExpect(jsonPath("$.shot[0].id", is(5)))
                .andExpect(jsonPath("$.shot[1].id", is(10)))

                .andExpect(jsonPath("$.other.", hasSize(1)))
                .andExpect(jsonPath("$.other[0].id", is(TEST_ID_1)))
                .andExpect(jsonPath("$.other[0].name", is(NAME)))
                .andExpect(jsonPath("$.other[0].description").doesNotExist())
                .andExpect(jsonPath("$.other[0].imageUrl", is(IMAGE_URL)))
                .andExpect(jsonPath("$.other[0].available", is("UNDEFINED")))
                .andExpect(jsonPath("$.other[0].relatedToMenu").doesNotExist())

                .andExpect(jsonPath("$.other[0].ingredients.beverages", hasSize(1)))
                .andExpect(jsonPath("$.other[0].ingredients.beverages[0].ingredientId", is(5)))
                .andExpect(jsonPath("$.other[0].ingredients.beverages[0].volume", is(TEST_VOLUME_VALUE)))
                .andExpect(jsonPath("$.other[0].ingredients.beverages[0].unitsValue", is(UnitsValue.ML.name())))
                .andExpect(jsonPath("$.other[0].ingredients.beverages[0].missing").doesNotExist());

        verify(cocktailsService, times(1)).getAllCocktails();
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void findAll_Should_ReturnFilteredCocktailEntries() throws Exception {

        final CocktailDto first = new CocktailDto();
        first.setId(5);

        final CocktailDto second = new CocktailDto();
        second.setId(10);

        when(cocktailsService.getAllCocktailsForMenu("any")).thenReturn(Lists.<ICocktail>newArrayList(first, second, createCocktailDto()));

        mockMvc.perform(get("/cocktails?filter=any"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(5)))
                .andExpect(jsonPath("$[0].available", is("UNDEFINED")))
                .andExpect(jsonPath("$[1].id", is(10)))
                .andExpect(jsonPath("$[2].id", is(TEST_ID_1)))
                .andExpect(jsonPath("$[2].name", is(NAME)))
                .andExpect(jsonPath("$[2].description").doesNotExist())
                .andExpect(jsonPath("$[2].imageUrl", is(IMAGE_URL)))
                .andExpect(jsonPath("$[2].available", is("UNDEFINED")))
                .andExpect(jsonPath("$[2].relatedToMenu").doesNotExist())

                .andExpect(jsonPath("$[2].ingredients.beverages", hasSize(1)))
                .andExpect(jsonPath("$[2].ingredients.beverages[0].ingredientId", is(5)))
                .andExpect(jsonPath("$[2].ingredients.beverages[0].volume", is(TEST_VOLUME_VALUE)))
                .andExpect(jsonPath("$[2].ingredients.beverages[0].unitsValue", is(UnitsValue.ML.name())))
                .andExpect(jsonPath("$[2].ingredients.beverages[0].missing").doesNotExist());

        verify(cocktailsService, times(1)).getAllCocktailsForMenu("any");
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void create_Should_CreateNewCocktail() throws Exception {
        CocktailDto cocktailDto = createCocktailDto();

        when(cocktailsService.saveCocktail(Matchers.any(ICocktail.class))).thenReturn(cocktailDto);
        String requestJson = toRequestJson(RestBeanConverter.toCocktailBean(cocktailDto));

        ResultActions resultActions = mockMvc.perform(post("/cocktails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept("application/json"))

                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        assertCocktailResponseBody(resultActions);

        verify(cocktailsService, times(1)).saveCocktail(Matchers.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void create_Should_ThrowMenuUnknown() throws Exception {

        when(cocktailsService.saveCocktail(Matchers.any(ICocktail.class))).thenThrow(new UnknownMenuException("unknown"));

        mockMvc.perform(post("/cocktails").contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .accept("application/json"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"Menu name [unknown] is unknown.")));

        verify(cocktailsService, times(1)).saveCocktail(Matchers.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void update_Should_UpdateCocktail() throws Exception {
        final CocktailDto cocktailDto = createCocktailDto();

        when(cocktailsService.updateCocktail(Matchers.any(ICocktail.class))).thenReturn(cocktailDto);

        String requestJson = toRequestJson(RestBeanConverter.toCocktailBean(cocktailDto));

        ResultActions resultActions = mockMvc.perform(put("/cocktails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept("application/json"))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        assertCocktailResponseBody(resultActions);

        verify(cocktailsService, times(1)).updateCocktail(Matchers.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsService);
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
                .andExpect(jsonPath("$.ingredients.beverages[0].unitsValue", is(UnitsValue.ML.name())))
                .andExpect(jsonPath("$.ingredients.beverages[0].missing").doesNotExist());
    }

    @Test
    public void update_Should_ThrowNotFound() throws Exception {

        when(cocktailsService.updateCocktail(Matchers.any(ICocktail.class))).thenThrow(new CocktailNotFoundException(TEST_ID_2));

        mockMvc.perform(put("/cocktails").contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .accept("application/json"))

                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"There is no cocktail with id: " + TEST_ID_2)));

        verify(cocktailsService, times(1)).updateCocktail(Matchers.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void update_Should_ThrowMenuUnknown() throws Exception {

        when(cocktailsService.updateCocktail(Matchers.any(ICocktail.class))).thenThrow(new UnknownMenuException("unknown"));

        mockMvc.perform(put("/cocktails").contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .accept("application/json"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"Menu name [unknown] is unknown.")));

        verify(cocktailsService, times(1)).updateCocktail(Matchers.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void delete_Should_DeleteCocktail() throws Exception {

        doNothing().when(cocktailsService).deleteCocktailById(TEST_ID_1);

        mockMvc.perform(delete("/cocktails/" + TEST_ID_1).accept("application/json"))

                .andDo(print())
                .andExpect(status().isNoContent());

        verify(cocktailsService, times(1)).deleteCocktailById(TEST_ID_1);
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void delete_Should_ThrowNotFound() throws Exception {

        doThrow(new CocktailNotFoundException(TEST_ID_2)).when(cocktailsService).deleteCocktailById(TEST_ID_2);

        mockMvc.perform(delete("/cocktails/" + TEST_ID_2).accept("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"There is no cocktail with id: " + TEST_ID_2)));

        verify(cocktailsService, times(1)).deleteCocktailById(TEST_ID_2);
        verifyNoMoreInteractions(cocktailsService);
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
        beverage.setUnitsValue(UnitsValue.ML);

        cocktailDto.setIngredients(ImmutableMap.<String, Collection<CocktailToIngredientDto>>of(IBeverage.GROUP_NAME, Collections.singleton(beverage)));
        return cocktailDto;
    }

}