package mybar.repository.bar;

import com.google.common.base.Predicate;
import com.google.common.collect.ContiguousSet;
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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.DiscreteDomain.integers;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Range.closed;
import static mybar.repository.bar.MenuDaoTest.assertExistedCocktail;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.*;

/**
 * Deep Tests of Cocktail DAO.
 */
public class CocktailDaoTest extends BaseDaoTest {

    public static final int TEST_ID_OF_COCKTAIL_WITH_INGREDIENTS = 1;
    public static final int TEST_ID_OF_COCKTAIL_WITH_NO_INGREDIENTS = 9;

    public static final List<Integer> INGREDIENTS_TEST_IDS = newArrayList(ContiguousSet.create(closed(1, 18), integers()));

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
        assertExistedCocktail(cocktailIterator.next(), 1, 1, "B52", State.AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 2, 1, "B53", State.AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 3, 1, "Green Mexican", State.AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 4, 1, "Blow Job", State.NOT_AVAILABLE);
        assertEquals("Number of CocktailToIngredient rows should be same.", 14, getCocktailToIngredientCount());

        // test second menu
        cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the second [{0}] menu should be same.", menuList.get(1).getName()), 6, cocktails.size());

        // test third menu
        cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the third [{0}] menu should be same.", menuList.get(2).getName()), 1, cocktails.size());

        // check that ingredients remain same
        assertThat("Ingredient list should remain the same.", findAllIngredientIds(), equalTo(INGREDIENTS_TEST_IDS));
    }

    @Test
    public void testThrowLikesExistExceptionWhenRemove() throws Exception {
        // TODO not for the first release
    }

    @Test
    public void testUpdateCocktailAndAddNewIngredients() throws Exception {
        Cocktail cocktail = cocktailDao.read(TEST_ID_OF_COCKTAIL_WITH_NO_INGREDIENTS); // Edit 'Mai Tai' cocktail and put it into 'smoothie' menu
        assertNotNull(cocktail);

        cocktail.setName("Random smoothie name");
        cocktail.setDescription("Random smoothie description");
        cocktail.setState(State.NOT_AVAILABLE);
        cocktail.setImageUrl("http://test-url.image.jpg");
        Menu menu = new Menu();
        menu.setId(3);
        cocktail.setMenu(menu);

        // add cocktail to ingredients relation
        CocktailToIngredient juice = new CocktailToIngredient();
        Drink ingredient1 = new Drink();
        ingredient1.setId(13);
        ingredient1.setKind("Orange Juice");
        juice.setIngredient(ingredient1);
        juice.setVolume(250);
        juice.setUnitsValue(UnitsValue.ML);

        CocktailToIngredient grenadine = new CocktailToIngredient();
        Additive ingredient2 = new Additive();
        ingredient2.setId(12);
        ingredient2.setKind("Grenadine");
        grenadine.setIngredient(ingredient2);
        grenadine.setVolume(10);
        grenadine.setUnitsValue(UnitsValue.ML);

        cocktail.addCocktailToIngredient(juice);
        cocktail.addCocktailToIngredient(grenadine);

        cocktailDao.update(cocktail);

        // check saved cocktail
        assertEquals("Number of CocktailToIngredient rows should be increased by two ingredients.", 19, getCocktailToIngredientCount());
        Cocktail updatedCocktail = findCocktailById(TEST_ID_OF_COCKTAIL_WITH_NO_INGREDIENTS);
        em.refresh(updatedCocktail); // TODO (temp solution) check potential issue with refreshing of related ingredients and menu
        List<CocktailToIngredient> cocktailToIngredientList = updatedCocktail.getCocktailToIngredientList();
        assertEquals("Number of ingredients in cocktail should be same.", 2, cocktailToIngredientList.size());

        assertCocktailToIngredient(cocktailToIngredientList, "Orange Juice", 13, 250, UnitsValue.ML, Drink.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Grenadine", 12, 10, UnitsValue.ML, Additive.class);

        assertEquals("Menu ID related to cocktail should be same.", 3, updatedCocktail.getMenu().getId());
        assertEquals("Menu NAME related to cocktail should be same.", "smoothie", updatedCocktail.getMenu().getName());
        assertEquals("Cocktail name should same.", "Random smoothie name", updatedCocktail.getName());
        assertEquals("Cocktail state should be same.", State.NOT_AVAILABLE, updatedCocktail.getState());
        assertEquals("Cocktail description should be same.", "Random smoothie description", updatedCocktail.getDescription());
        assertEquals("Cocktail image url should be same.", "http://test-url.image.jpg", updatedCocktail.getImageUrl());

        // check that ingredients remain same
        assertThat("Ingredient list should remain the same.", findAllIngredientIds(), equalTo(INGREDIENTS_TEST_IDS));
    }

    @Test
    public void testUpdateCocktailWhenChangeIngredients() throws Exception {
        Cocktail cocktail = cocktailDao.read(TEST_ID_OF_COCKTAIL_WITH_INGREDIENTS); // Edit 'Mai Tai' cocktail and put it into 'smoothie' menu
        assertNotNull(cocktail);
        List<CocktailToIngredient> cocktailToIngredientList = cocktail.getCocktailToIngredientList();
        assertEquals("Number of ingredients in cocktail should be same.", 3, cocktailToIngredientList.size());

        assertCocktailToIngredient(cocktailToIngredientList, "Triple Sec", 8, 20, UnitsValue.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Irish cream", 11, 20, UnitsValue.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Coffee liqueur", 16, 20, UnitsValue.ML, Beverage.class);

        Cocktail cocktailForUpdate = new Cocktail();
        cocktailForUpdate.setId(cocktail.getId());
        cocktailForUpdate.setName(cocktail.getName());
        cocktailForUpdate.setDescription(cocktail.getDescription());
        cocktailForUpdate.setState(cocktail.getState());
        cocktailForUpdate.setImageUrl(cocktail.getImageUrl());
        cocktailForUpdate.setMenu(cocktail.getMenu());

        // add cocktail to ingredients relation
        CocktailToIngredient juice = new CocktailToIngredient();
        Drink ingredient1 = new Drink();
        ingredient1.setId(13);
        ingredient1.setKind("Orange Juice");
        juice.setIngredient(ingredient1);
        juice.setVolume(250);
        juice.setUnitsValue(UnitsValue.ML);

        CocktailToIngredient grenadine = new CocktailToIngredient();
        Additive ingredient2 = new Additive();
        ingredient2.setId(12);
        ingredient2.setKind("Grenadine");
        grenadine.setIngredient(ingredient2);
        grenadine.setVolume(10);
        grenadine.setUnitsValue(UnitsValue.ML);

        cocktailForUpdate.addCocktailToIngredient(juice);
        cocktailForUpdate.addCocktailToIngredient(grenadine);

        cocktailDao.update(cocktailForUpdate);

        // check saved cocktail
        assertEquals("Number of CocktailToIngredient rows should be decrease by 1 ingredient [3 removed, 2 new added].", 16, getCocktailToIngredientCount());
        Cocktail updatedCocktail = findCocktailById(TEST_ID_OF_COCKTAIL_WITH_INGREDIENTS);
        cocktailToIngredientList = updatedCocktail.getCocktailToIngredientList();
        assertEquals("Number of ingredients in cocktail should be same.", 2, cocktailToIngredientList.size());

        assertCocktailToIngredient(cocktailToIngredientList, "Orange Juice", 13, 250, UnitsValue.ML, Drink.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Grenadine", 12, 10, UnitsValue.ML, Additive.class);

        // check that ingredients remain same
        assertThat("Ingredient list should remain the same.", findAllIngredientIds(), equalTo(INGREDIENTS_TEST_IDS));
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
        assertEquals("Number of ingredients in cocktail should be same.", 8, cocktailToIngredientList.size());

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
        assertThat("Ingredient class type should be same.", cocktailToIngredient.getIngredient(), isA(type));
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

    private int getCocktailToIngredientCount() {
        String sql = "SELECT count(cti) FROM CocktailToIngredient cti";
        Query q = em.createQuery(sql);
        Long count = (Long) q.getSingleResult();
        return count.intValue();
    }

    protected Cocktail findCocktailById(int id) {
        TypedQuery<Cocktail> q = em.createQuery("SELECT c FROM Cocktail c WHERE c.id = :id", Cocktail.class);
        q.setParameter("id", id);
        Cocktail result = q.getSingleResult();
        return result;
    }

    protected List<Integer> findAllIngredientIds() {
        Query q = em.createQuery("SELECT i.id FROM Ingredient i");
        List<Integer> result = q.getResultList();
        return result;
    }

}