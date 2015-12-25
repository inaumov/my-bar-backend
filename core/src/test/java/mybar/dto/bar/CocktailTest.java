package mybar.dto.bar;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import mybar.State;
import mybar.UnitsValue;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IInside;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Inside;
import mybar.domain.bar.Menu;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

        cocktail.setInsideList(Lists.newArrayList(beverage, juice));

        Menu menu = new Menu();
        menu.setId(MENU_ID);

        cocktail.setMenu(menu);
        cocktail.setDescription(DESCRIPTION);
        cocktail.setState(State.AVAILABLE);
        cocktail.setImageUrl(IMAGE_URL);

        ICocktail dto = cocktail.toDto();
        assertEquals(TEST_ID, dto.getId());
        assertEquals(IMAGE_URL, dto.getImageUrl());
        assertEquals(MENU_ID, dto.getMenuId());
        assertEquals(NAME, dto.getName());

        //Map<String, Collection<? extends IInside>> insideList = dto.getInsideList();
        Map<String, ? extends Collection<? extends IInside>> insides = dto.getInsides();

/*
        IInside beverageDto = Iterables.find(insideList, new Predicate<IInside>() {
            @Override
            public boolean apply(IInside iInside) {
                return iInside.getIngredientId() == 1;
            }
        });
        assertEquals(1, beverageDto.getIngredientId());
        assertEquals(50, beverageDto.getVolume(), 0);
        assertEquals(UnitsValue.ML, beverageDto.getUnitsValue());

        IInside juiceDto = Iterables.find(insideList, new Predicate<IInside>() {
            @Override
            public boolean apply(IInside iInside) {
                return iInside.getIngredientId() == 25;
            }
        });
        assertEquals(25, juiceDto.getIngredientId());
        assertEquals(150, juice.getVolume(), 0);
        assertEquals(UnitsValue.ML, beverageDto.getUnitsValue());
*/
    }

}