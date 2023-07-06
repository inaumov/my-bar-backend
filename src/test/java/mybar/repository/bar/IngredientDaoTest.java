package mybar.repository.bar;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import lombok.SneakyThrows;
import mybar.api.bar.ingredient.BeverageType;
import mybar.api.bar.ingredient.IBeverage;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.repository.BaseDaoTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Ingredient DAO.
 */
@DatabaseSetup("classpath:datasets/dataset.xml")
@ContextConfiguration(classes = IngredientDao.class)
public class IngredientDaoTest extends BaseDaoTest {

    private static final Integer THIRD_ITEM = 3;
    private static final Integer LAST_ITEM = 16;

    private static final int EXPECTED_BEVERAGES_NMB = 12;

    private static final String BACARDI_BOTTLE_ID = "bottle-000003";
    private static final String HAVANA_CLUB_BOTTLE_ID = "bottle-000007";

    private static final String[] INGREDIENTS_BY_GROUP_NAME_AND_KIND = {
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
    @ExpectedDatabase(value = "classpath:datasets/dataset.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPreconditions() {
        // do nothing, just load and check dataSet and context loads
        assertThat(ingredientDao).isNotNull();
    }

    @Test
    public void testFindAllOrdered() {

        List<Ingredient> all = ingredientDao.findAll();
        // Number of ingredients should be 18.
        org.assertj.core.api.Assertions.assertThat(all)
                .withFailMessage("Number of ingredients should be 18.")
                .hasSize(18);

        // assert ordering
        List<String> ingredientsByKind = all
                .stream()
                .map(Ingredient::getKind)
                .collect(Collectors.toList());
        assertThat(ingredientsByKind)
                .withFailMessage("All ingredients should be sorted by group name and kind.")
                .containsExactly(INGREDIENTS_BY_GROUP_NAME_AND_KIND);
    }

    @Test
    public void testFindByIds_When_AllExists() {

        List<Ingredient> all = ingredientDao.findIn(List.of(1, 18, 17));

        assertEquals(3, all.size());
        assertEquals("Vodka", all.get(0).getKind());
        assertEquals("Coca Cola", all.get(1).getKind());
        assertEquals("Lime", all.get(2).getKind());
    }

    @Test
    public void testFindByIds_When_TwoExists() {
        List<Integer> ids = IntStream.rangeClosed(17, 25)
                .boxed()
                .toList();
        List<Ingredient> all = ingredientDao.findIn(ids);

        assertEquals(all.size(), 2);
        assertEquals("Coca Cola", all.get(0).getKind());
        assertEquals("Lime", all.get(1).getKind());
    }

    @SneakyThrows
    @Test
    public void testFindBeverages() {

        List<Ingredient> beverages = ingredientDao.findByGroupName(IBeverage.GROUP_NAME);

        assertNotNull(beverages, "Ingredient list is null.");
        assertEquals(EXPECTED_BEVERAGES_NMB, beverages.size());

        Optional<Ingredient> thirdIngredient = beverages
                .stream()
                .filter(b -> Objects.equals(b.getId(), THIRD_ITEM))
                .findFirst();
        assertTrue(thirdIngredient.isPresent(), "Ingredient is null.");
        assertIngredient(thirdIngredient.get(), THIRD_ITEM, "Rum");
        Beverage beverage = (Beverage) thirdIngredient.get();
        List<Bottle> bottles = beverage.getBottles();
        assertNotNull(bottles);
        org.assertj.core.api.Assertions.assertThat(bottles)
                .withFailMessage("Number of bottles assigned to ingredient should be same.")
                .hasSize(2);
        assertBottle(BACARDI_BOTTLE_ID, "Bacardi", true, bottles.get(0));
        assertBottle(HAVANA_CLUB_BOTTLE_ID, "Havana Club", false, bottles.get(1));

        Optional<Ingredient> lastIngredient = beverages
                .stream()
                .filter(b -> Objects.equals(LAST_ITEM, b.getId())
                ).findFirst();
        assertTrue(lastIngredient.isPresent(), "Ingredient is null.");
        assertIngredient(lastIngredient.get(), LAST_ITEM, "Coffee liqueur");
    }

    private void assertIngredient(Ingredient ingredient, Integer item, String expected) {
        assertTrue(ingredient instanceof Beverage, "Ingredient should be an instance of Beverage.");
        Beverage beverage = (Beverage) ingredient;
        assertEquals(item, beverage.getId());
        assertEquals(expected, beverage.getKind(), "Ingredient kind name should be same.");
        assertEquals(BeverageType.DISTILLED, beverage.getBeverageType(), "Beverage type should be same.");
    }

    private void assertBottle(String id, String expectedBrand, boolean inShelf, Bottle bottle) {
        assertNotNull(bottle);

        assertEquals(id, bottle.getId(), "Bottle id should be same.");
        assertEquals(inShelf, bottle.isInShelf(), "Bottle in shelf.");
        assertEquals(expectedBrand, bottle.getBrandName(), "Bottle brand name should be same.");
    }

    @Test
    public void testFindBeverageById() {
        Beverage beverageById = ingredientDao.findBeverageById(5);
        assertNotNull(beverageById);
        assertEquals("Bourbon", beverageById.getKind());
    }

    @Test
    public void testFindBeverageById_when_is_not_this_type() {
        assertNull(ingredientDao.findBeverageById(18));
    }

}