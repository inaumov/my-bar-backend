package mybar.web.rest.bar;

import mybar.api.bar.ingredient.*;
import mybar.dto.bar.ingredient.AdditiveDto;
import mybar.dto.bar.ingredient.BeverageDto;
import mybar.dto.bar.ingredient.DrinkDto;
import mybar.service.bar.IngredientService;
import mybar.web.rest.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
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
        additive.setId(1);
        additive.setKind("Lime");

        DrinkDto drink = new DrinkDto();
        drink.setId(2);
        drink.setKind("Black tea");
        drink.setDrinkType(DrinkType.TEA);

        BeverageDto beverage = new BeverageDto();
        beverage.setId(3);
        beverage.setKind("Whiskey");
        beverage.setBeverageType(BeverageType.DISTILLED);

        when(ingredientService.findAll()).thenReturn(Arrays.<IIngredient>asList(additive, drink, beverage));

        mockMvc.perform(get("/ingredients"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("additives.items.", hasSize(1)))
                .andExpect(jsonPath("additives.items.[0].id", is(1)))
                .andExpect(jsonPath("additives.items.[0].kind", is("Lime")))

                .andExpect(jsonPath("drinks.items.", hasSize(1)))
                .andExpect(jsonPath("drinks.items.[0].id", is(2)))
                .andExpect(jsonPath("drinks.items.[0].kind", is("Black tea")))
                .andExpect(jsonPath("drinks.items.[0].drinkType", is(DrinkType.TEA.name())))

                .andExpect(jsonPath("beverages.items.", hasSize(1)))
                .andExpect(jsonPath("beverages.items.[0].id", is(3)))
                .andExpect(jsonPath("beverages.items.[0].kind", is("Whiskey")))
                .andExpect(jsonPath("beverages.items.[0].beverageType", is(BeverageType.DISTILLED.name())));

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

        mockMvc.perform(get("/ingredients?filter=additives"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("measurements").exists())
                .andExpect(jsonPath("items.", hasSize(2)))

                .andExpect(jsonPath("items.[0].id", is(1)))
                .andExpect(jsonPath("items.[0].kind", is("Ice")))

                .andExpect(jsonPath("items.[1].id", is(2)))
                .andExpect(jsonPath("items.[1].kind", is("Lime")));

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

        mockMvc.perform(get("/ingredients?filter=beverages"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("measurements").exists())
                .andExpect(jsonPath("items.", hasSize(2)))

                .andExpect(jsonPath("items.[0].id", is(3)))
                .andExpect(jsonPath("items.[0].kind", is("Whiskey")))

                .andExpect(jsonPath("items.[1].id", is(7)))
                .andExpect(jsonPath("items.[1].kind", is("Rum")));

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

        when(ingredientService.findByGroupName(DRINKS)).thenReturn(Arrays.asList(drink11, drink2));

        mockMvc.perform(get("/ingredients?filter=drinks"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("measurements").exists())
                .andExpect(jsonPath("items.", hasSize(2)))

                .andExpect(jsonPath("items.[0].id", is(11)))
                .andExpect(jsonPath("items.[0].kind", is("Golden tips")))

                .andExpect(jsonPath("items.[1].id", is(2)))
                .andExpect(jsonPath("items.[1].kind", is("Black tea")));

        verify(ingredientService, times(1)).findByGroupName(DRINKS);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    public void findByGroupName_Should_ReturnEmptyResponse_When_FilterByGroup() throws Exception {

        when(ingredientService.findByGroupName("drinks")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/ingredients?filter=drinks"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("measurements.", empty()))
                .andExpect(jsonPath("items.", empty()))
                .andExpect(jsonPath("isLiquid").doesNotExist());

        verify(ingredientService, times(1)).findByGroupName("drinks");
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    public void findByGroupName_Should_ThrowIllegalArgumentException_When_FilterByUnknown() throws Exception {

        when(ingredientService.findByGroupName(UNKNOWN)).thenThrow(new IllegalArgumentException("Unknown group name: " + UNKNOWN));

        mockMvc.perform(get("/ingredients?filter=unknown"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"Unknown group name: " + UNKNOWN)));

        verify(ingredientService, times(1)).findByGroupName("unknown");
        verifyNoMoreInteractions(ingredientService);
    }

}