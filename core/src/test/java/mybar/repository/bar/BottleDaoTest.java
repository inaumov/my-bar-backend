package mybar.repository.bar;

import mybar.State;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.Ingredient;
import mybar.repository.BaseDaoTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class BottleDaoTest extends BaseDaoTest {

    @Autowired
    private BottleDao bottleDao;

    @Test
    public void testFindAll() throws Exception {
        List<Bottle> all = bottleDao.findAll();
        assertEquals(7, all.size());
        assertBottles(1, all);
    }

    @Test
    public void testDestroyAll() throws Exception {
        int count = bottleDao.destroyAll();
        assertEquals(7, count);
    }

    @Test
    public void testReadById() {
        Bottle bottle = bottleDao.read(1);
        assertTrue(bottle.getBrandName().equals(""));
    }

    @Test
    public void testCreateBottle() throws Exception {
        Bottle bottle = new Bottle();
        bottle.setBrandName("Johny Walker");
        bottle.setPrice(289);
        bottle.setState(State.AVAILABLE);
        bottle.setImageUrl("http://whiskey.last.jpg");
        Ingredient ingredientRef = em.getReference(Ingredient.class, 6);
        bottle.setIngredient(ingredientRef);

        Bottle saved = bottleDao.create(bottle);
        assertFalse(saved.getId() == 0);
        List<Bottle> all = bottleDao.findAll();
        assertEquals(8, all.size());
        assertBottles(1, all);
    }

    @Test
    public void testUpdateBottle() throws Exception {
        Bottle bottle = bottleDao.read(7);

        // assert existing bottle
        assertLast(bottle);

        // update retrieved bottle
        Ingredient ingredient = new Ingredient();
        ingredient.setId(6);
        bottle.setIngredient(ingredient);
        bottle.setBrandName("Johny Walker");
        bottle.setPrice(289);
        bottle.setState(State.AVAILABLE);
        bottle.setImageUrl("http://whiskey.last.jpg");

        // assert updated bottle
        Bottle updated = bottleDao.update(bottle);
        assertEquals("Johny Walker", updated.getBrandName());
        assertEquals(6, updated.getIngredient().getId());
        assertEquals(State.AVAILABLE, updated.getState());
        assertTrue(updated.getImageUrl().contains("whiskey"));
    }

    @Test
    public void testDeleteBottle() throws Exception {
        bottleDao.delete(1);
        List<Bottle> all = bottleDao.findAll();
        assertEquals(6, all.size());
        assertBottles(2, all);
    }

    private void assertBottles(long startFromId, List<Bottle> all) {
        Iterator<Bottle> it = all.iterator();
        for (long id = startFromId; id <= all.size(); id++) {
            assertEquals(id, it.next().getId());
        }
    }

    private void assertLast(Bottle bottle) {
        assertEquals(7, bottle.getId());
        assertEquals(3, bottle.getIngredient().getId());
        assertEquals("Havana Club", bottle.getBrandName());
        assertEquals(0.5, bottle.getVolume(), 0);
        assertEquals(119, bottle.getPrice(), 0);
        assertEquals(State.NOT_AVAILABLE, bottle.getState());
        assertTrue(bottle.getImageUrl().contains("rum"));
    }

}