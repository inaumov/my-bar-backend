package mybar.repository.bar;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import mybar.api.bar.ingredient.BeverageType;
import mybar.api.bar.ingredient.IBeverage;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.repository.BaseDaoTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests Ingredient DAO.
 */
@DatabaseSetup("classpath:datasets/dataset.xml")
public class IngredientDaoTest extends BaseDaoTest {

    private static final Integer THIRD_ITEM = 3;
    private static final Integer LAST_ITEM = 16;

    private static final int EXPECTED_BEVERAGES_NMB = 12;

    private static final String BACARDI_BOTTLE_ID = "bottle-000003";
    private static final String HAVANA_CLUB_BOTTLE_ID = "bottle-000007";

    private static String[] INGREDIENTS_BY_GROUP_NAME_AND_KIND = {
            // Additives
            "Grenadine",
            "Ice",
            "Lime",
            "Sugar",
            // Beverages
            "Banana liqueur",
            "Bourbon",
            "Brandy",
            "Coffee liqueur",
            "Gin",
            "Irish cream",
            "Maraschino liqueur",
            "Rum",
            "Tequila",
            "Triple Sec",
            "Vodka",
            "Whisky",
            // Drinks
            "Coca Cola",
            "Orange Juice"
    };

    @Autowired
    private IngredientDao ingredientDao;

    @Test
    public void testFindAllInCorrectOrder() throws Exception {
        assertNotNull("Ingredient DAO is null.", ingredientDao);

        List<Ingredient> all = ingredientDao.findAll();
        assertThat("Number of ingredients should be 18.", all, hasSize(18));

        // assert ordering
        List<String> ingredientsByKind = all
                .stream()
                .map(Ingredient::getKind)
                .collect(Collectors.toList());
        assertThat("All ingredients should be sorted by group name and kind.", ingredientsByKind, contains(INGREDIENTS_BY_GROUP_NAME_AND_KIND));
    }

    @Test
    public void testFindAllInWrongOrder() throws Exception {
        assertNotNull("Ingredient DAO is null.", ingredientDao);

        List<Ingredient> all = ingredientDao.findAll();

        // assert ordering
        List<String> ingredientsByKind = all
                .stream()
                .map(Ingredient::getKind)
                .collect(Collectors.toList());
        // make it sorted alphabetically so it is wrong
        String[] items = INGREDIENTS_BY_GROUP_NAME_AND_KIND.clone();
        Arrays.sort(items);
        assertThat("All ingredients should be sorted by group name and kind.", ingredientsByKind, not(contains(items)));
    }

    @Test
    public void testFindByIds_When_AllExists() throws Exception {
        assertNotNull("Ingredient DAO is null.", ingredientDao);

        List<Ingredient> all = ingredientDao.findIn(Lists.newArrayList(1, 18, 17));

        assertTrue(all.size() == 3);
        assertEquals("Vodka", all.get(0).getKind());
        assertEquals("Coca Cola", all.get(1).getKind());
        assertEquals("Lime", all.get(2).getKind());
    }

    @Test
    public void testFindByIds_When_TwoExists() throws Exception {
        assertNotNull("Ingredient DAO is null.", ingredientDao);

        List<Ingredient> all = ingredientDao.findIn(ContiguousSet.create(Range.closedOpen(17, 25), DiscreteDomain.integers()).asList());

        assertTrue(all.size() == 2);
        assertEquals("Coca Cola", all.get(0).getKind());
        assertEquals("Lime", all.get(1).getKind());
    }

    @Test
    public void testFindBeverages() throws Exception {
        assertNotNull("Ingredient DAO is null.", ingredientDao);

        List<Ingredient> beverages = ingredientDao.findByGroupName(IBeverage.GROUP_NAME);

        assertNotNull("Ingredient list is null.", beverages);
        assertEquals(EXPECTED_BEVERAGES_NMB, beverages.size());

        Optional<Ingredient> thirdIngredient = beverages.stream().filter(b -> Objects.equals(b.getId(), THIRD_ITEM)).findFirst();
        assertTrue("Ingredient is null.", thirdIngredient.isPresent());
        assertIngredient(thirdIngredient.get(), THIRD_ITEM, "Rum");
        Beverage beverage = (Beverage) thirdIngredient.get();
        List<Bottle> bottles = beverage.getBottles();
        assertNotNull(beverage.getBottles());
        assertEquals("Number of bottles assigned to ingredient should be same.", 2, beverage.getBottles().size());
        assertBottle(BACARDI_BOTTLE_ID, "Bacardi", true, bottles.get(0));
        assertBottle(HAVANA_CLUB_BOTTLE_ID, "Havana Club", false, bottles.get(1));

        Optional<Ingredient> lastIngredient = beverages.stream().filter(b -> Objects.equals(LAST_ITEM, b.getId())).findFirst();
        assertTrue("Ingredient is null.", lastIngredient.isPresent());
        assertIngredient(lastIngredient.get(), LAST_ITEM, "Coffee liqueur");
    }

    private void assertIngredient(Ingredient ingredient, Integer item, String expected) {
        assertTrue("Ingredient should be an instance of Beverage.", ingredient instanceof Beverage);
        Beverage beverage = (Beverage) ingredient;
        assertEquals(item, beverage.getId());
        assertEquals("Ingredient kind name should be same.", expected, beverage.getKind());
        assertEquals("Beverage type should be same.", BeverageType.DISTILLED, beverage.getBeverageType());
    }

    private void assertBottle(String id, String expectedBrand, boolean inShelf, Bottle bottle) {
        assertNotNull(bottle);

        assertEquals("Bottle id should be same.", id, bottle.getId());
        assertEquals("Bottle in shelf.", inShelf, bottle.isInShelf());
        assertEquals("Bottle brand name should be same.", expectedBrand, bottle.getBrandName());
    }

    @Test
    public void testFindBeverageById() throws Exception {
        Beverage beverageById = ingredientDao.findBeverageById(5);
        assertNotNull(beverageById);
        assertEquals("Bourbon", beverageById.getKind());
    }

    @Test
    public void testFindBeverageById_when_is_not_this_type() throws Exception {
        assertNull(ingredientDao.findBeverageById(18));
    }

}