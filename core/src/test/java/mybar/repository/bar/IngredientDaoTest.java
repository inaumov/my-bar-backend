package mybar.repository.bar;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import mybar.BeverageType;
import mybar.State;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.repository.BaseDaoTest;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 * Tests Ingredient DAO.
 */
public class IngredientDaoTest extends BaseDaoTest {

    private static List<String> items = Splitter.on(";").splitToList("");

    @Autowired
    private IngredientDao ingredientDao;

    @Test
    public void testFindAll() throws Exception {
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
        assertThat("All ingredients should be sorted by groupName and kind.", ingredientsByKind, IsIterableContainingInOrder.contains(items)));
    }

    @Test
    public void testFindBeverages() throws Exception {
        assertNotNull("Ingredient DAO is null.", ingredientDao);

        List<Ingredient> beverages = ingredientDao.findByGroupName("Beverage");

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

                        assertEquals("Bottle state should be same.", State.AVAILABLE, bottle.getState());
                        assertEquals("Bottle brand name should be same.", "Bacardi", bottle.getBrandName());
                    } else if (havanaClub.equals(bottle.getId())) {
                        assertEquals("Bottle id should be same.", havanaClub.intValue(), bottle.getId());

                        assertEquals("Bottle state should be same.", State.NOT_AVAILABLE, bottle.getState());
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