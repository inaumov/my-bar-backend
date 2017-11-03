package mybar.repository.bar;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.base.Predicate;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Iterables;
import mybar.api.bar.Measurement;
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
@DatabaseSetup("classpath:dataset.xml")
public class CocktailDaoTest extends BaseDaoTest {

    public static final String TEST_REF_OF_COCKTAIL_WITH_INGREDIENTS = "cocktail-000001";
    public static final String TEST_REF_OF_COCKTAIL_WITH_NO_INGREDIENTS = "cocktail-000009";

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
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000001", 1, "B52");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000002", 1, "B53");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000003", 1, "Green Mexican");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000004", 1, "Blow Job");
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
        Cocktail cocktail = cocktailDao.read(TEST_REF_OF_COCKTAIL_WITH_NO_INGREDIENTS); // Edit 'Mai Tai' cocktail and put it into 'smoothie' menu
        assertNotNull(cocktail);

        cocktail.setName("Random smoothie name");
        cocktail.setDescription("Random smoothie description");
        cocktail.setImageUrl("http://test-url.image.jpg");
        cocktail.setMenuId(3);

        // add cocktail to ingredients relation
        CocktailToIngredient juice = new CocktailToIngredient();
        Drink ingredient1 = new Drink();
        ingredient1.setId(13);
        ingredient1.setKind("Orange Juice");
        juice.setIngredient(ingredient1);
        juice.setVolume(250);
        juice.setMeasurement(Measurement.ML);

        CocktailToIngredient grenadine = new CocktailToIngredient();
        Additive ingredient2 = new Additive();
        ingredient2.setId(12);
        ingredient2.setKind("Grenadine");
        grenadine.setIngredient(ingredient2);
        grenadine.setVolume(10);
        grenadine.setMeasurement(Measurement.ML);

        cocktail.addCocktailToIngredient(juice);
        cocktail.addCocktailToIngredient(grenadine);

        cocktailDao.update(cocktail);

        // check saved cocktail
        assertEquals("Number of CocktailToIngredient rows should be increased by two ingredients.", 19, getCocktailToIngredientCount());
        Cocktail updatedCocktail = findCocktailById(TEST_REF_OF_COCKTAIL_WITH_NO_INGREDIENTS);
        em.refresh(updatedCocktail); // TODO (temp solution) check potential issue with refreshing of related ingredients and menu
        List<CocktailToIngredient> cocktailToIngredientList = updatedCocktail.getCocktailToIngredientList();
        assertEquals("Number of ingredients in cocktail should be same.", 2, cocktailToIngredientList.size());

        assertCocktailToIngredient(cocktailToIngredientList, "Orange Juice", 13, 250, Measurement.ML, Drink.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Grenadine", 12, 10, Measurement.ML, Additive.class);

        assertEquals("Menu ID related to cocktail should be same.", 3, updatedCocktail.getMenuId());
        assertEquals("Cocktail name should same.", "Random smoothie name", updatedCocktail.getName());
        assertEquals("Cocktail description should be same.", "Random smoothie description", updatedCocktail.getDescription());
        assertEquals("Cocktail image url should be same.", "http://test-url.image.jpg", updatedCocktail.getImageUrl());

        // check that ingredients remain same
        assertThat("Ingredient list should remain the same.", findAllIngredientIds(), equalTo(INGREDIENTS_TEST_IDS));
    }

    @Test
    public void testUpdateCocktailWhenChangeIngredients() throws Exception {
        Cocktail cocktail = cocktailDao.read(TEST_REF_OF_COCKTAIL_WITH_INGREDIENTS); // Edit 'Mai Tai' cocktail and put it into 'smoothie' menu
        assertNotNull(cocktail);
        List<CocktailToIngredient> cocktailToIngredientList = cocktail.getCocktailToIngredientList();
        assertEquals("Number of ingredients in cocktail should be same.", 3, cocktailToIngredientList.size());

        assertCocktailToIngredient(cocktailToIngredientList, "Triple Sec", 8, 20, Measurement.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Irish cream", 11, 20, Measurement.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Coffee liqueur", 16, 20, Measurement.ML, Beverage.class);

        Cocktail cocktailForUpdate = new Cocktail();
        cocktailForUpdate.setId(cocktail.getId());
        cocktailForUpdate.setName(cocktail.getName());
        cocktailForUpdate.setDescription(cocktail.getDescription());
        cocktailForUpdate.setImageUrl(cocktail.getImageUrl());
        cocktailForUpdate.setMenuId(cocktail.getMenuId());

        // add cocktail to ingredients relation
        CocktailToIngredient juice = new CocktailToIngredient();
        Drink ingredient1 = new Drink();
        ingredient1.setId(13);
        ingredient1.setKind("Orange Juice");
        juice.setIngredient(ingredient1);
        juice.setVolume(250);
        juice.setMeasurement(Measurement.ML);

        CocktailToIngredient grenadine = new CocktailToIngredient();
        Additive ingredient2 = new Additive();
        ingredient2.setId(12);
        ingredient2.setKind("Grenadine");
        grenadine.setIngredient(ingredient2);
        grenadine.setVolume(10);
        grenadine.setMeasurement(Measurement.ML);

        cocktailForUpdate.addCocktailToIngredient(juice);
        cocktailForUpdate.addCocktailToIngredient(grenadine);

        cocktailDao.update(cocktailForUpdate);

        // check saved cocktail
        assertEquals("Number of CocktailToIngredient rows should be decrease by 1 ingredient [3 removed, 2 new added].", 16, getCocktailToIngredientCount());
        Cocktail updatedCocktail = findCocktailById(TEST_REF_OF_COCKTAIL_WITH_INGREDIENTS);
        cocktailToIngredientList = updatedCocktail.getCocktailToIngredientList();
        assertEquals("Number of ingredients in cocktail should be same.", 2, cocktailToIngredientList.size());

        assertCocktailToIngredient(cocktailToIngredientList, "Orange Juice", 13, 250, Measurement.ML, Drink.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Grenadine", 12, 10, Measurement.ML, Additive.class);

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

        assertCocktailToIngredient(cocktailToIngredientList, "Vodka", 1, 20, Measurement.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Gin", 2, 20, Measurement.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Rum", 3, 20, Measurement.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Tequila", 4, 20, Measurement.ML, Beverage.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Whisky", 6, 20, Measurement.ML, Beverage.class);

        assertCocktailToIngredient(cocktailToIngredientList, "Coca Cola", 17, 150, Measurement.ML, Drink.class);

        assertCocktailToIngredient(cocktailToIngredientList, "Ice", 14, 5, Measurement.PCS, Additive.class);
        assertCocktailToIngredient(cocktailToIngredientList, "Lime", 18, 5, Measurement.PCS, Additive.class);
    }

    private static void assertCocktailToIngredient(List<CocktailToIngredient> cocktailToIngredientList,
                                                   String ingredientName,
                                                   int ingredientId,
                                                   int expectedVolume,
                                                   Measurement measurement, Class type) {
        CocktailToIngredient cocktailToIngredient = findCocktailToIngredientByIngredientName(cocktailToIngredientList, ingredientName);
        assertEquals("Ingredient ID should be same.", ingredientId, cocktailToIngredient.getIngredient().getId());
        assertEquals("Volume of ingredient should be same.", expectedVolume, cocktailToIngredient.getVolume(), 0);
        assertEquals("Units value of ingredient should be same.", measurement, cocktailToIngredient.getMeasurement());
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

    protected Cocktail findCocktailById(String id) {
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