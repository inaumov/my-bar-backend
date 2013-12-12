package mybar.dao;

import mybar.ActiveStatus;
import mybar.DishType;
import mybar.entity.Category;
import mybar.entity.Dish;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MenuTest extends BaseDaoTest {

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private DishDAO dishDAO;

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
    public void testGetDishesForCategory() throws Exception {
        List<Category> list = categoryDAO.findAll();
        Iterator<Category> it = list.iterator();

        // test first category
        Collection<Dish> dishes = it.next().getDishes();
        assertEquals(4, dishes.size());
        Iterator<Dish> dish = dishes.iterator();
        assertDish(dish.next(), 1, 1, "B52", DishType.KITCHEN, ActiveStatus.ENABLED);
        assertDish(dish.next(), 2, 1, "B53", DishType.KITCHEN, ActiveStatus.ENABLED);
        assertDish(dish.next(), 3, 1, "Green Mexican", DishType.KITCHEN, ActiveStatus.ENABLED);
        assertDish(dish.next(), 4, 1, "Blow Job", DishType.KITCHEN, ActiveStatus.DISABLED);

        // second category
        dishes = it.next().getDishes();
        assertEquals(7, dishes.size());

        // third category
        dishes = it.next().getDishes();
        assertEquals(2, dishes.size());
        dish = dishes.iterator();
        assertDish(dish.next(), 12, 3, "Americano", DishType.NON_KITCHEN, ActiveStatus.ENABLED);

        // forth category
        dishes = it.next().getDishes();
        assertEquals(4, dishes.size());
    }

    private void assertDish(Dish dish, int id, int categoryId, String name, DishType dishType, ActiveStatus status) {
        assertEquals(id, dish.getId());
        assertEquals(categoryId, dish.getCategory().getId());
        assertEquals(name, dish.getName());
        assertEquals(dishType, dish.getDishType());
        assertEquals(status, dish.getActiveStatus());
    }

    @Test
    public void testSaveDish() throws Exception {
        Category firstCategory = categoryDAO.findAll().iterator().next();
        for (int i = 99; i < 109; i++) {
            Dish dish = new Dish();
            dish.setCategory(firstCategory);
            dish.setDishType(DishType.KITCHEN);
            dish.setName("Dish " + (char) i);
            dish.setActiveStatus(ActiveStatus.ENABLED);
            dishDAO.create(dish);

            // Now persists the category dish relationship
            Collection<Dish> dishes = firstCategory.getDishes();
            if (dishes != null)
                dishes.add(dish);
            categoryDAO.update(firstCategory);
        }
        assertEquals(14, getAndAssertDishes().size());
    }

    @Test
    public void testRemoveDishWhenNoSales() throws Exception {
        Category firstCategory = categoryDAO.findAll().iterator().next();
        firstCategory.getDishes().clear();
        categoryDAO.update(firstCategory);
    }

    @Test
    public void testThrowSalesExistExceptionWhenRemove() throws Exception {

    }

    @Test
    public void testEditDish() throws Exception {

    }

    @Test
    public void testGetBasisForDish() throws Exception {

    }

    protected Collection<Dish> getAndAssertDishes() {
        Category c = categoryDAO.findAll().iterator().next();
        Collection<Dish> dishList = c.getDishes();
        assertDishList(dishList);
        return dishList;
    }

    protected void assertDishList(Collection<Dish> all) {
        Iterator<Dish> it = all.iterator();
        for (int id = 1; id <= all.size(); id++) {
            Dish dish = it.next();
            assertNotNull(dish.getId());
            assertNotNull(dish.getName());
            assertNotNull(dish.getDishType());
            assertNotNull(dish.getActiveStatus());
            assertEquals(1, dish.getCategory().getId());
        }
    }

}