package mybar.repository.bar;

import com.google.common.base.Function;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests Ingredient DAO.
 */
public class IngredientDaoTest extends BaseDaoTest {

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
        List<String> ingredientsByKind = Lists.transform(all, new Function<Ingredient, String>() {
            @Override
            public String apply(Ingredient ingredient) {
                return ingredient.getKind();
            }
        });
        assertThat("All ingredients should be sorted by group name and kind.", ingredientsByKind, contains(INGREDIENTS_BY_GROUP_NAME_AND_KIND));
    }

    @Test
    public void testFindAllInWrongOrder() throws Exception {
        assertNotNull("Ingredient DAO is null.", ingredientDao);

        List<Ingredient> all = ingredientDao.findAll();

        // assert ordering
        List<String> ingredientsByKind = Lists.transform(all, new Function<Ingredient, String>() {
            @Override
            public String apply(Ingredient ingredient) {
                return ingredient.getKind();
            }
        });
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

        int expected = 12;

        assertNotNull("Ingredient list is null.", beverages);
        assertEquals("Number of ingredients should be same.", expected, beverages.size());

        Integer third = new Integer(3);
        Integer last = new Integer(16);

        for (Ingredient ingredient : beverages) {
            assertNotNull("Ingredient is null.", ingredient);

            if (third.equals(ingredient.getId())) {

                String kind = "Rum";
                BeverageType beverageType = BeverageType.DISTILLED;

                int expectedBottles = 2;

                assertEquals("Ingredient kind name should be same.", kind, ingredient.getKind());
                assertTrue("Ingredient should be an instance of Beverage.", ingredient instanceof Beverage);
                Beverage beverage = (Beverage) ingredient;

                assertNotNull("List of bottles assigned to ingredient is null.", beverage.getBottles());
                assertEquals("Number of bottles assigned to ingredient should be same.", expectedBottles, beverage.getBottles().size());

                assertEquals("Beverage type should be same.", beverageType, beverage.getBeverageType());

                Integer bacardi = new Integer(3);
                Integer havanaClub = new Integer(7);

                for (Bottle bottle : beverage.getBottles()) {
                    assertNotNull("Bottle is null.", bottle);

                    if (bacardi.equals(bottle.getId())) {
                        assertEquals("Bottle id should be same.", bacardi.intValue(), bottle.getId());

                        assertTrue("Bottle in shelf.", bottle.isInShelf());
                        assertEquals("Bottle brand name should be same.", "Bacardi", bottle.getBrandName());
                    } else if (havanaClub.equals(bottle.getId())) {
                        assertEquals("Bottle id should be same.", havanaClub.intValue(), bottle.getId());

                        assertFalse("Bottle not in shelf.", bottle.isInShelf());
                        assertEquals("Bottle brand name should be same.", "Havana Club", bottle.getBrandName());
                    }
                }

            } else if (last.equals(ingredient.getId())) {

                String kind = "Coffee liqueur";
                BeverageType beverageType = BeverageType.DISTILLED;

                int expectedBottles = 0;

                assertEquals("Ingredient kind name should be same.", kind, ingredient.getKind());
                assertTrue("Ingredient should be an instance of Beverage.", ingredient instanceof Beverage);
                Beverage beverage = (Beverage) ingredient;

                assertNotNull("List of bottles assigned to ingredient is null.", beverage.getBottles());
                assertEquals("Number of bottles assigned to ingredient should be same.", expectedBottles, beverage.getBottles().size());

                assertEquals("Beverage type should be same.", beverageType, beverage.getBeverageType());
            }
        }
    }

}