package mybar.repository.bar;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import mybar.api.bar.ingredient.BeverageType;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.repository.BaseDaoTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Tests Bottle DAO.
 */
public class BottleDaoTest extends BaseDaoTest {

    private static final BigDecimal PRICE = new BigDecimal(119.00);
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
        Bottle bottle = bottleDao.read("bottle-000001");
        assertTrue(bottle.getBrandName().equals("Absolute"));
    }

    @Test
    public void testBeverageRelatedToBottle() {
        Bottle bottle = bottleDao.read("bottle-000001");
        assertEquals(bottle.getBeverage().getId(), 1);
        assertEquals(bottle.getBeverage().getKind(), "Vodka");
        assertEquals(bottle.getBeverage().getBeverageType(), BeverageType.DISTILLED);
    }

    @Test
    public void testGetBottlesByBeverage() {

        Bottle bottle = bottleDao.read("bottle-000003");
        assertEquals(bottle.getBeverage().getId(), 3);
        assertEquals(bottle.getBeverage().getKind(), "Rum");
        assertEquals(bottle.getBeverage().getBeverageType(), BeverageType.DISTILLED);

        List<Bottle> bottlesByBeverage = bottle.getBeverage().getBottles();
        assertEquals(2, bottlesByBeverage.size());

        Bottle first = Iterables.find(bottlesByBeverage, new Predicate<Bottle>() {
            @Override
            public boolean apply(Bottle bottle) {
                return bottle.getBrandName().equals("Bacardi");
            }
        });
        assertNotNull(first);
        Bottle second = Iterables.find(bottlesByBeverage, new Predicate<Bottle>() {
            @Override
            public boolean apply(Bottle bottle) {
                return bottle.getBrandName().equals("Havana Club");
            }
        });
        assertNotNull(second);
    }

    @Test
    public void testCreateBottle() throws Exception {
        Bottle bottle = new Bottle();
        bottle.setBrandName("Johny Walker");
        bottle.setPrice(new BigDecimal(289));
        bottle.setInShelf(true);
        bottle.setImageUrl("http://whiskey.last.jpg");
        Beverage beverageRef = em.getReference(Beverage.class, 6);
        bottle.setBeverage(beverageRef);

        Bottle saved = bottleDao.create(bottle);
        assertFalse(Strings.isNullOrEmpty(saved.getId()));
        List<Bottle> all = bottleDao.findAll();
        assertEquals(8, all.size());
        assertBottles(1, all); // TODO until switch to HSQLDB because derby doesn't work with identity generated value for ID columns.
    }

    @Test
    public void testUpdateBottle() throws Exception {
        Bottle bottle = bottleDao.read("bottle-000007");

        // assert existing bottle
        assertLast(bottle);

        // update retrieved bottle
        Beverage beverage = new Beverage();
        beverage.setId(6);
        bottle.setBeverage(beverage);
        bottle.setBrandName("Johny Walker");
        bottle.setPrice(new BigDecimal(289));
        bottle.setInShelf(true);
        bottle.setImageUrl("http://whiskey.last.jpg");

        // assert updated bottle
        Bottle updated = bottleDao.update(bottle);
        assertEquals("Johny Walker", updated.getBrandName());
        assertEquals(6, updated.getBeverage().getId());
        assertTrue(updated.isInShelf());
        assertTrue(updated.getImageUrl().contains("whiskey"));
    }

    @Test
    public void testDeleteBottle() throws Exception {
        bottleDao.delete("bottle-000001");
        List<Bottle> all = bottleDao.findAll();
        assertEquals(6, all.size());
        assertBottles(2, all);
    }

    private void assertBottles(int startFromId, List<Bottle> all) {
        Iterator<Bottle> it = all.iterator();
        for (int id = startFromId; id < all.size(); id++) {
            assertEquals("bottle-00000" + id, it.next().getId());
        }
        assertTrue(it.next().getId().contains("bottle-"));
    }

    private void assertLast(Bottle bottle) {
        assertEquals("bottle-000007", bottle.getId());
        assertEquals(3, bottle.getBeverage().getId());
        assertEquals("Havana Club", bottle.getBrandName());
        assertEquals(0.5, bottle.getVolume(), 0);
        assertThat(PRICE, Matchers.comparesEqualTo(bottle.getPrice()));
        assertFalse(bottle.isInShelf());
        assertTrue(bottle.getImageUrl().contains("rum"));
    }

}