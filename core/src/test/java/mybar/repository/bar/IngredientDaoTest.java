package mybar.repository.bar;

import mybar.BeverageType;
import mybar.State;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.repository.BaseDaoTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests Ingredient DAO.
 */
public class IngredientDaoTest extends BaseDaoTest {

    @Autowired
    private IngredientDao ingredientDao;

    @Test
    public void testFindAll() throws Exception {
        assertNotNull("Ingredient DAO is null.", ingredientDao);

        List<Ingredient> all = ingredientDao.findAll();
        assertEquals(18, all.size());
    }

    @Test
    public void testFindBeverages() throws Exception {
        assertNotNull("Ingredient DAO is null.", ingredientDao);

        List<Ingredient> beverages = ingredientDao.findByGroupName("Beverage");

        int expected = 12;

        assertNotNull("Ingredient list is null.", beverages);
        assertEquals("Number of ingredients should be " + expected + ".", expected, beverages.size());

        Integer third = new Integer(3);
        Integer last = new Integer(16);

        for (Ingredient ingredient : beverages) {
            assertNotNull("Ingredient is null.", ingredient);

            if (third.equals(ingredient.getId())) {

                String kind = "Rum";
                String groupName = "Beverage";
                BeverageType beverageType = BeverageType.DISTILLED;

                int expectedBottles = 2;

                assertEquals("Ingredient kind name should be " + kind + ".", kind, ingredient.getKind());
                assertEquals("Ingredient group name should be " + groupName + ".", groupName, ingredient.getGroupName());

                assertTrue("Ingredient should be an instance of beverage.", (ingredient instanceof Beverage));
                Beverage beverage = (Beverage) ingredient;

                assertNotNull("List of bottles assigned to ingredient is null.", beverage.getBottles());
                assertEquals("Number of bottles assigned to ingredient should be " + expectedBottles + ".", expectedBottles, beverage.getBottles().size());

                assertEquals("Beverage type should be " + beverageType.name() + ".", beverageType, beverage.getBeverageType());

                Integer bacardi = new Integer(3);
                Integer havanaClub = new Integer(7);

                for (Bottle bottle : beverage.getBottles()) {
                    assertNotNull("Bottle is null.", bottle);

                    if (bacardi.equals(bottle.getId())) {
                        assertEquals("Bottle id should be '" + bacardi + "'.", bacardi.intValue(), bottle.getId());

                        assertEquals("Bottle state should be '" + State.AVAILABLE + "'.", State.AVAILABLE, bottle.getState());
                        assertEquals("Bottle brand name should be '" + "Bacardi" + "'.", "Bacardi", bottle.getBrandName());
                    } else if (havanaClub.equals(bottle.getId())) {
                        assertEquals("Bottle id should be '" + havanaClub + "'.", havanaClub.intValue(), bottle.getId());

                        assertEquals("Bottle state should be '" + State.NOT_AVAILABLE + "'.", State.NOT_AVAILABLE, bottle.getState());
                        assertEquals("Bottle brand name should be '" + "Havana Club" + "'.", "Havana Club", bottle.getBrandName());

                    }
                }

            } else if (last.equals(ingredient.getId())) {

                String kind = "Coffee liqueur";
                String groupName = "Beverage";
                BeverageType beverageType = BeverageType.DISTILLED;

                int expectedBottles = 0;

                assertEquals("Ingredient kind name should be " + kind + ".", kind, ingredient.getKind());
                assertEquals("Ingredient group name should be " + groupName + ".", groupName, ingredient.getGroupName());

                assertTrue("Ingredient should be an instance of beverage.", (ingredient instanceof Beverage));
                Beverage beverage = (Beverage) ingredient;

                assertNotNull("List of bottles assigned to ingredient is null.", beverage.getBottles());
                assertEquals("Number of bottles assigned to ingredient should be " + expectedBottles + ".", expectedBottles, beverage.getBottles().size());

                assertEquals("Beverage type should be " + beverageType.name() + ".", beverageType, beverage.getBeverageType());
            }
        }
    }

}