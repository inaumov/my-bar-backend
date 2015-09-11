package mybar.dao;

import mybar.ActiveStatus;
import mybar.Preparation;
import mybar.entity.Menu;
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
    private MenuDao menuDao;

    @Autowired
    private DrinkDAO drinkDao;

    @Test
    public void testSelectAllMenus() throws Exception {
        List<Menu> list = menuDao.findAll();
        assertEquals(4, list.size());
        Iterator<Menu> it = list.iterator();
        assertMenu(it.next(), 1, "Shooter");
        assertMenu(it.next(), 2, "Long");
        assertMenu(it.next(), 3, "Coffee");
        assertMenu(it.next(), 4, "Tea");
    }

    private void assertMenu(Menu c, int id, String name) {
        assertEquals(id, c.getId());
        assertEquals(name, c.getName());
    }

    @Test
    public void testGetDrinksForMenu() throws Exception {
        List<Menu> list = menuDao.findAll();
        Iterator<Menu> it = list.iterator();

        // test first menu
        Collection<Drink> drinks = it.next().getDrinks();
        assertEquals(4, drinks.size());
        Iterator<Drink> drink = drinks.iterator();
        assertDrink(drink.next(), 1, 1, "B52", Preparation.KITCHEN, ActiveStatus.ENABLED);
        assertDrink(drink.next(), 2, 1, "B53", Preparation.KITCHEN, ActiveStatus.ENABLED);
        assertDrink(drink.next(), 3, 1, "Green Mexican", Preparation.KITCHEN, ActiveStatus.ENABLED);
        assertDrink(drink.next(), 4, 1, "Blow Job", Preparation.KITCHEN, ActiveStatus.DISABLED);

        // second menu
        drinks = it.next().getDrinks();
        assertEquals(7, drinks.size());

        // third menu
        drinks = it.next().getDrinks();
        assertEquals(2, drinks.size());
        drink = drinks.iterator();
        assertDrink(drink.next(), 12, 3, "Americano", Preparation.NON_KITCHEN, ActiveStatus.ENABLED);

        // forth menu
        drinks = it.next().getDrinks();
        assertEquals(4, drinks.size());
    }

    private void assertDrink(Drink drink, int id, int menuId, String name, Preparation preparation, ActiveStatus status) {
        assertEquals(id, drink.getId());
        assertEquals(menuId, drink.getMenu().getId());
        assertEquals(name, drink.getName());
        assertEquals(preparation, drink.getPreparation());
        assertEquals(status, drink.getActiveStatus());
    }

    @Test
    public void testSaveDrink() throws Exception {
        Menu firstMenu = menuDao.findAll().iterator().next();
        for (int i = 99; i < 109; i++) {
            Drink drink = new Drink();
            drink.setMenu(firstMenu);
            drink.setPreparation(Preparation.KITCHEN);
            drink.setName("Drink " + (char) i);
            drink.setActiveStatus(ActiveStatus.ENABLED);
            drinkDao.create(drink);

            // Now persists the menu drink relationship
            Collection<Drink> drinks = firstMenu.getDrinks();
            if (drinks != null)
                drinks.add(drink);
            menuDao.update(firstMenu);
        }
        assertEquals(14, getAndAssertDrinks().size());
    }

    @Test
    public void testRemoveDrinkWhenNoSales() throws Exception {
        Menu firstMenu = menuDao.findAll().iterator().next();
        firstMenu.getDrinks().clear();
        menuDao.update(firstMenu);
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
        Menu c = menuDao.findAll().iterator().next();
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
            assertEquals(1, drink.getMenu().getId());
        }
    }

}