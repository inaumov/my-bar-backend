package mybar.dao;

import mybar.ActiveStatus;
import mybar.Preparation;
import mybar.entity.Category;
import mybar.entity.Drink;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MenuTest extends BaseDaoTest {

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private DrinkDAO drinkDAO;

    @Test
    public void testSelectAllCategories() throws Exception {
        List<Category> list = categoryDAO.findAll();
        assertEquals(4, list.size());
        Iterator<Category> it = list.iterator();
        assertCategory(it.next(), 1, "Shooter");
        assertCategory(it.next(), 2, "Long");
        assertCategory(it.next(), 3, "Coffee");
        assertCategory(it.next(), 4, "Tea");
    }

    private void assertCategory(Category c, int id, String name) {
        assertEquals(id, c.getId());
        assertEquals(name, c.getName());
    }

    @Test
    public void testGetDrinksForCategory() throws Exception {
        List<Category> list = categoryDAO.findAll();
        Iterator<Category> it = list.iterator();

        // test first category
        Collection<Drink> drinks = it.next().getDrinks();
        assertEquals(4, drinks.size());
        Iterator<Drink> drink = drinks.iterator();
        assertDrink(drink.next(), 1, 1, "B52", Preparation.KITCHEN, ActiveStatus.ENABLED);
        assertDrink(drink.next(), 2, 1, "B53", Preparation.KITCHEN, ActiveStatus.ENABLED);
        assertDrink(drink.next(), 3, 1, "Green Mexican", Preparation.KITCHEN, ActiveStatus.ENABLED);
        assertDrink(drink.next(), 4, 1, "Blow Job", Preparation.KITCHEN, ActiveStatus.DISABLED);

        // second category
        drinks = it.next().getDrinks();
        assertEquals(7, drinks.size());

        // third category
        drinks = it.next().getDrinks();
        assertEquals(2, drinks.size());
        drink = drinks.iterator();
        assertDrink(drink.next(), 12, 3, "Americano", Preparation.NON_KITCHEN, ActiveStatus.ENABLED);

        // forth category
        drinks = it.next().getDrinks();
        assertEquals(4, drinks.size());
    }

    private void assertDrink(Drink drink, int id, int categoryId, String name, Preparation preparation, ActiveStatus status) {
        assertEquals(id, drink.getId());
        assertEquals(categoryId, drink.getCategory().getId());
        assertEquals(name, drink.getName());
        assertEquals(preparation, drink.getPreparation());
        assertEquals(status, drink.getActiveStatus());
    }

    @Test
    public void testSaveDrink() throws Exception {
        Category firstCategory = categoryDAO.findAll().iterator().next();
        for (int i = 99; i < 109; i++) {
            Drink drink = new Drink();
            drink.setCategory(firstCategory);
            drink.setPreparation(Preparation.KITCHEN);
            drink.setName("Drink " + (char) i);
            drink.setActiveStatus(ActiveStatus.ENABLED);
            drinkDAO.create(drink);

            // Now persists the category drink relationship
            Collection<Drink> drinks = firstCategory.getDrinks();
            if (drinks != null)
                drinks.add(drink);
            categoryDAO.update(firstCategory);
        }
        assertEquals(14, getAndAssertDrinks().size());
    }

    @Test
    public void testRemoveDrinkWhenNoSales() throws Exception {
        Category firstCategory = categoryDAO.findAll().iterator().next();
        firstCategory.getDrinks().clear();
        categoryDAO.update(firstCategory);
    }

    @Test
    public void testThrowSalesExistExceptionWhenRemove() throws Exception {

    }

    @Test
    public void testEditDrink() throws Exception {

    }

    @Test
    public void testGetBasisForDrink() throws Exception {

    }

    protected Collection<Drink> getAndAssertDrinks() {
        Category c = categoryDAO.findAll().iterator().next();
        Collection<Drink> drinkList = c.getDrinks();
        assertDrinkList(drinkList);
        return drinkList;
    }

    protected void assertDrinkList(Collection<Drink> all) {
        Iterator<Drink> it = all.iterator();
        for (int id = 1; id <= all.size(); id++) {
            Drink drink = it.next();
            assertNotNull(drink.getId());
            assertNotNull(drink.getName());
            assertNotNull(drink.getPreparation());
            assertNotNull(drink.getActiveStatus());
            assertEquals(1, drink.getCategory().getId());
        }
    }

}