package mybar.dto.bar;

import com.google.common.collect.Lists;
import mybar.UnitOfMeasurement;
import mybar.api.bar.ICocktail;
import mybar.api.bar.ICocktailIngredient;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.CocktailToIngredient;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CocktailTest {

    public static final String TEST_REFERENCE = "cocktail-123";
    public static final int MENU_ID = 2;
    public static final String MENU_NAME = "TEST_NAME";
    public static final String NAME = "Rum Cola";
    public static final String DESCRIPTION = "Loren ipsum";
    public static final String IMAGE_URL = "http://cocktail-image.jpg";

    @Test
    public void testConvertCocktailToDto() throws Exception {
        Cocktail cocktail = new Cocktail();
        cocktail.setId(TEST_REFERENCE);
        cocktail.setName(NAME);

        CocktailToIngredient beverage = new CocktailToIngredient();
        beverage.setIngredient(new Beverage(1));
        beverage.setVolume(50);
        beverage.setUnitOfMeasurement(UnitOfMeasurement.ML);

        CocktailToIngredient juice = new CocktailToIngredient();
        Drink drink = new Drink();
        drink.setId(25);
        juice.setIngredient(drink);
        juice.setVolume(150);
        juice.setUnitOfMeasurement(UnitOfMeasurement.ML);

        cocktail.setCocktailToIngredientList(Lists.newArrayList(beverage, juice));
        cocktail.setMenuId(MENU_ID);
        cocktail.setDescription(DESCRIPTION);
        cocktail.setImageUrl(IMAGE_URL);

        ICocktail dto = cocktail.toDto(MENU_NAME);
        assertEquals(TEST_REFERENCE, dto.getId());
        assertEquals(IMAGE_URL, dto.getImageUrl());
        assertEquals(MENU_NAME, dto.getMenuName());
        assertEquals(NAME, dto.getName());
        assertEquals(DESCRIPTION, dto.getDescription());

        Map<String, ? extends Collection<? extends ICocktailIngredient>> insideList = dto.getIngredients();
        assertEquals(2, insideList.size());
        assertTrue(insideList.containsKey("beverages"));
        assertTrue(insideList.containsKey("drinks"));

        Collection<? extends ICocktailIngredient> beverages = insideList.get("beverages");
        assertEquals(1, beverages.size());

        ICocktailIngredient beverageDto = beverages.iterator().next();
        assertEquals(1, beverageDto.getIngredientId());
        assertEquals(50, beverageDto.getVolume(), 0);
        assertEquals(UnitOfMeasurement.ML, beverageDto.getUnitOfMeasurement());

        Collection<? extends ICocktailIngredient> drinks = insideList.get("drinks");
        assertEquals(1, drinks.size());

        ICocktailIngredient juiceDto = drinks.iterator().next();
        assertEquals(25, juiceDto.getIngredientId());
        assertEquals(150, juice.getVolume(), 0);
        assertEquals(UnitOfMeasurement.ML, beverageDto.getUnitOfMeasurement());
    }

}