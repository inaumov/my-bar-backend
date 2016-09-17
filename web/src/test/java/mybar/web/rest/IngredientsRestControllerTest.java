package mybar.web.rest;

import mybar.BeverageType;
import mybar.DrinkType;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.dto.bar.ingredient.AdditiveDto;
import mybar.dto.bar.ingredient.BeverageDto;
import mybar.dto.bar.ingredient.DrinkDto;
import mybar.service.bar.IngredientService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-rest-context.xml")
public class IngredientsRestControllerTest {

    public static final String ADDITIVES = IAdditive.GROUP_NAME;
    public static final String BEVERAGES = IBeverage.GROUP_NAME;
    public static final String DRINKS = IDrink.GROUP_NAME;
    public static final String UNKNOWN = "unknown";
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private IngredientService ingredientService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
        Mockito.reset(ingredientService);
    }

    @Test
    public void findAll_Should_ReturnAll() throws Exception {

        AdditiveDto additive = new AdditiveDto();
        DrinkDto drink = new DrinkDto();
        BeverageDto beverage = new BeverageDto();
        additive.setId(1);
        additive.setKind("Lime");
        drink.setId(2);
        drink.setKind("Black tea");
        drink.setDrinkType(DrinkType.TEA);
        beverage.setId(3);
        beverage.setKind("Whiskey");
        beverage.setBeverageType(BeverageType.DISTILLED);

        when(ingredientService.findAll()).thenReturn(Arrays.<IIngredient>asList(additive, drink, beverage));

        mockMvc.perform(get("/ingredients").accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("additives.$", hasSize(1)))

                .andExpect(jsonPath("additives.$[0].id", is(1)))
                .andExpect(jsonPath("additives.$[0].kind", is("Lime")))

                .andExpect(jsonPath("drinks.$", hasSize(1)))

                .andExpect(jsonPath("drinks.$[0].id", is(2)))
                .andExpect(jsonPath("drinks.$[0].kind", is("Black tea")))
                .andExpect(jsonPath("drinks.$[0].drinkType", is(DrinkType.TEA.name())))

                .andExpect(jsonPath("beverages.$", hasSize(1)))

                .andExpect(jsonPath("beverages.$[0].id", is(3)))
                .andExpect(jsonPath("beverages.$[0].kind", is("Whiskey")))
                .andExpect(jsonPath("beverages.$[0].beverageType", is(BeverageType.DISTILLED.name())));

        verify(ingredientService, times(1)).findAll();
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    public void findByGroupName_Should_ReturnOnlyAdditives() throws Exception {

        AdditiveDto additive1 = new AdditiveDto();
        AdditiveDto additive2 = new AdditiveDto();
        additive1.setId(1);
        additive1.setKind("Ice");
        additive2.setId(2);
        additive2.setKind("Lime");

        when(ingredientService.findByGroupName(ADDITIVES)).thenReturn(Arrays.<IIngredient>asList(additive1, additive2));

        mockMvc.perform(get("/ingredients?filter=additives").accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("additives.$", hasSize(2)))

                .andExpect(jsonPath("additives.$[0].id", is(1)))
                .andExpect(jsonPath("additives.$[0].kind", is("Ice")))

                .andExpect(jsonPath("additives.$[1].id", is(2)))
                .andExpect(jsonPath("additives.$[1].kind", is("Lime")));

        verify(ingredientService, times(1)).findByGroupName(ADDITIVES);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    public void findByGroupName_Should_ReturnOnlyBeverages() throws Exception {

        BeverageDto beverage3 = new BeverageDto();
        beverage3.setId(3);
        beverage3.setKind("Whiskey");
        beverage3.setBeverageType(BeverageType.DISTILLED);

        BeverageDto beverage7 = new BeverageDto();
        beverage7.setId(7);
        beverage7.setKind("Rum");
        beverage7.setBeverageType(BeverageType.DISTILLED);

        when(ingredientService.findByGroupName(BEVERAGES)).thenReturn(Arrays.<IIngredient>asList(beverage3, beverage7));

        mockMvc.perform(get("/ingredients?filter=beverages").accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("beverages.$", hasSize(2)))

                .andExpect(jsonPath("beverages.$[0].id", is(3)))
                .andExpect(jsonPath("beverages.$[0].kind", is("Whiskey")))

                .andExpect(jsonPath("beverages.$[1].id", is(7)))
                .andExpect(jsonPath("beverages.$[1].kind", is("Rum")));

        verify(ingredientService, times(1)).findByGroupName(BEVERAGES);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    public void findByGroupName_Should_ReturnOnlyDrinks() throws Exception {

        DrinkDto drink2 = new DrinkDto();
        drink2.setId(2);
        drink2.setKind("Black tea");
        drink2.setDrinkType(DrinkType.TEA);
        DrinkDto drink11 = new DrinkDto();
        drink11.setId(11);
        drink11.setKind("Golden tips");
        drink11.setDrinkType(DrinkType.TEA);

        when(ingredientService.findByGroupName(DRINKS)).thenReturn(Arrays.<IIngredient>asList(drink11, drink2));

        mockMvc.perform(get("/ingredients?filter=drinks").accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("drinks.$", hasSize(2)))

                .andExpect(jsonPath("drinks.$[0].id", is(11)))
                .andExpect(jsonPath("drinks.$[0].kind", is("Golden tips")))

                .andExpect(jsonPath("drinks.$[1].id", is(2)))
                .andExpect(jsonPath("drinks.$[1].kind", is("Black tea")));

        verify(ingredientService, times(1)).findByGroupName(DRINKS);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    public void findByGroupName_Should_ReturnNothing_When_FilterByUnknown() throws Exception {

        when(ingredientService.findByGroupName(UNKNOWN)).thenReturn(Collections.<IIngredient>emptyList());

        mockMvc.perform(get("/ingredients?filter=unknown"))

                .andExpect(status().isNoContent())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$", empty()));

        verify(ingredientService, times(1)).findByGroupName("unknown");
        verifyNoMoreInteractions(ingredientService);
    }

}