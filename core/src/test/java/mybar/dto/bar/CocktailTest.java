package mybar.dto.bar;

import com.google.common.collect.Lists;
import mybar.State;
import mybar.UnitsValue;
import mybar.api.bar.IInside;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Inside;
import mybar.domain.bar.Menu;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CocktailTest {

    public static final int TEST_ID = 1;
    public static final int MENU_ID = 2;
    public static final String NAME = "Rum Cola";
    public static final String DESCRIPTION = "Loren ipsum";
    public static final String IMAGE_URL = "http://cocktail-image.jpg";

    @Test
    public void testConvertCocktailToDto() throws Exception {
        Cocktail cocktail = new Cocktail();
        cocktail.setId(TEST_ID);
        cocktail.setName(NAME);

        Inside beverage = new Inside();
        beverage.setId(1);
        beverage.setIngredient(new Beverage(1));
        beverage.setVolume(50);
        beverage.setUnitsValue(UnitsValue.ML);

        Inside juice = new Inside();
        juice.setId(TEST_ID);
        Drink drink = new Drink();
        drink.setId(25);
        juice.setIngredient(drink);
        juice.setVolume(150);
        juice.setUnitsValue(UnitsValue.ML);

        cocktail.setInsideItems(Lists.newArrayList(beverage, juice));

        Menu menu = new Menu();
        menu.setId(MENU_ID);

        cocktail.setMenu(menu);
        cocktail.setDescription(DESCRIPTION);
        cocktail.setState(State.AVAILABLE);
        cocktail.setImageUrl(IMAGE_URL);

        CocktailDto dto = cocktail.toDto();
        assertEquals(TEST_ID, dto.getId());
        assertEquals(IMAGE_URL, dto.getImageUrl());
        assertEquals(MENU_ID, dto.getMenuId());
        assertEquals(NAME, dto.getName());
        assertEquals(DESCRIPTION, dto.getDescription());

        Map<String, Collection<InsideDto>> insideList = dto.getInsideItems();
        assertEquals(2, insideList.size());
        assertTrue(insideList.containsKey("beverages"));
        assertTrue(insideList.containsKey("drinks"));

        Collection<InsideDto> beverages = insideList.get("beverages");
        assertEquals(1, beverages.size());

        IInside beverageDto = beverages.iterator().next();
        assertEquals(1, beverageDto.getIngredientId());
        assertEquals(50, beverageDto.getVolume(), 0);
        assertEquals(UnitsValue.ML, beverageDto.getUnitsValue());

        Collection<InsideDto> drinks = insideList.get("drinks");
        assertEquals(1, drinks.size());

        IInside juiceDto = drinks.iterator().next();
        assertEquals(25, juiceDto.getIngredientId());
        assertEquals(150, juice.getVolume(), 0);
        assertEquals(UnitsValue.ML, beverageDto.getUnitsValue());
    }

}