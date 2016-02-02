package mybar.repository.bar;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import mybar.State;
import mybar.UnitsValue;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.CocktailToIngredient;
import mybar.domain.bar.Menu;
import mybar.domain.bar.ingredient.Additive;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import mybar.repository.BaseDaoTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static mybar.repository.bar.MenuDaoTest.assertCocktail;
import static org.junit.Assert.*;

/**
 * Deep Tests of Cocktail DAO.
 */
public class CocktailDaoTest extends BaseDaoTest {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private CocktailDao cocktailDao;

    @Test
    public void testRemoveCocktailFromMenuWhenNoLikes() throws Exception {
        Menu secondMenu = menuDao.findAll().get(1);

        // Now persists the menu cocktail relationship
        Cocktail blackRussian = Iterables.find(secondMenu.getCocktails(), new Predicate<Cocktail>() {
            @Override
            public boolean apply(Cocktail cocktail) {
                return cocktail.getName().contains("Black Russian");
            }
        });
        cocktailDao.delete(blackRussian.getId());
        secondMenu.getCocktails().remove(blackRussian);
        menuDao.update(secondMenu);

        List<Menu> menuList = menuDao.findAll();
        Iterator<Menu> it = menuList.iterator();

        // test first menu
        Collection<Cocktail> cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the first [{0}] menu should be same.", menuList.get(0).getName()), 4, cocktails.size());
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), 1, 1, "B52", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 2, 1, "B53", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 3, 1, "Green Mexican", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 4, 1, "Blow Job", State.NOT_AVAILABLE);

        // test second menu
        cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the second [{0}] menu should be same.", menuList.get(1).getName()), 6, cocktails.size());

        // test third menu
        cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the third [{0}] menu should be same.", menuList.get(2).getName()), 1, cocktails.size());
    }

    @Test
    public void testThrowLikesExistExceptionWhenRemove() throws Exception {
        // TODO not for the first release
    }

    @Test
    public void testUpdateCocktail() throws Exception {

    }

    @Test
    public void testGetIngredientsForCocktail() throws Exception {
        Menu longMenu = menuDao.findAll().get(1);

        // Now persists the menu cocktail relationship
        Cocktail longIsland = Iterables.find(longMenu.getCocktails(), new Predicate<Cocktail>() {
            @Override
            public boolean apply(Cocktail cocktail) {
                return cocktail.getName().contains("Long Island Iced Tea");
            }
        });
        List<CocktailToIngredient> cocktailToIngredientList = longIsland.getCocktailToIngredientList();
        assertEquals("Number of ingredient in cocktail should be same.", 8, cocktailToIngredientList.size());

        assertCocktailToIngredient(cocktailToIngredientList, "Vodka", 1, 20, UnitsValue.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Gin", 2, 20, UnitsValue.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Rum", 3, 20, UnitsValue.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Tequila", 4, 20, UnitsValue.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Whisky", 6, 20, UnitsValue.ML, Beverage.class);

        assertCocktailToIngredient(cocktailToIngredientList, "Coca Cola", 17, 150, UnitsValue.ML, Drink.class);

        assertCocktailToIngredient(cocktailToIngredientList, "Ice", 14, 5, UnitsValue.PCS, Additive.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Lime", 18, 5, UnitsValue.PCS, Additive.class);
    }

    private static void assertCocktailToIngredient(List<CocktailToIngredient> cocktailToIngredientList,
                                                   String ingredientName,
                                                   int ingredientId,
                                                   int expectedVolume,
                                                   UnitsValue unitsValue, Class type) {
        CocktailToIngredient cocktailToIngredient = findCocktailToIngredientByIngredientName(cocktailToIngredientList, ingredientName);
        assertEquals("Ingredient ID should be same.", ingredientId, cocktailToIngredient.getIngredient().getId());
        assertEquals("Volume of ingredient should be same.", expectedVolume, cocktailToIngredient.getVolume(), 0);
        assertEquals("Units value of ingredient should be same.", unitsValue, cocktailToIngredient.getUnitsValue());
        assertThat("Ingredient class type should be same.", cocktailToIngredient.getIngredient(), Matchers.isA(type));
    }

    public static CocktailToIngredient findCocktailToIngredientByIngredientName(List<CocktailToIngredient> cocktailToIngredientList,
                                                                                final String ingredientName) {
        CocktailToIngredient cocktailToIngredient = Iterables.find(cocktailToIngredientList, new Predicate<CocktailToIngredient>() {
            @Override
            public boolean apply(CocktailToIngredient cocktailToIngredient) {
                return cocktailToIngredient.getIngredient().getKind().equals(ingredientName);
            }
        });
        assertNotNull(MessageFormat.format("Cocktail to ingredient should be present for ingredientName = {0}.", ingredientName), cocktailToIngredient);
        return cocktailToIngredient;
    }

}