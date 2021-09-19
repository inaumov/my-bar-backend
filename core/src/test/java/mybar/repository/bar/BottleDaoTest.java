package mybar.repository.bar;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.base.Strings;
import mybar.api.bar.ingredient.BeverageType;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.repository.BaseDaoTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Bottle DAO.
 */
@DatabaseSetup("classpath:datasets/dataset.xml")
public class BottleDaoTest extends BaseDaoTest {

    private static final BigDecimal PRICE = new BigDecimal("119.00");
    @Autowired
    private BottleDao bottleDao;

    @Test
    @ExpectedDatabase(value = "classpath:datasets/dataset.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPreconditions() throws Exception {
        // do nothing, just load and check dataSet
    }

    @Test
    public void testFindAll() throws Exception {
        List<Bottle> all = bottleDao.findAll();
        assertEquals(7, all.size());

        AtomicInteger id = new AtomicInteger();
        all.forEach(bottle -> assertEquals("bottle-00000" + id.incrementAndGet(), bottle.getId()));
    }

    @Test
    public void testDestroyAll() throws Exception {
        int count = bottleDao.destroyAll();
        assertEquals(7, count);
    }

    @Test
    public void testReadById() {
        Bottle bottle = bottleDao.read("bottle-000007");

        assertLast(bottle);
    }

    @Test
    public void testGetBottlesByBeverage() {
        Bottle bottle = bottleDao.read("bottle-000003");

        assertEquals(3, bottle.getBeverage().getId().intValue());
        assertEquals("Rum", bottle.getBeverage().getKind());
        assertEquals(BeverageType.DISTILLED, bottle.getBeverage().getBeverageType());

        List<Bottle> bottlesByBeverage = bottle.getBeverage().getBottles();
        assertEquals(2, bottlesByBeverage.size());

        Optional<Bottle> bacardi = bottlesByBeverage
                .stream()
                .filter(b -> b.getBrandName().equals("Bacardi"))
                .findAny();
        assertTrue(bacardi.isPresent());

        Optional<Bottle> second = bottlesByBeverage
                .stream()
                .filter(b -> b.getBrandName().equals("Havana Club"))
                .findAny();
        assertTrue(second.isPresent());
    }

    @ExpectedDatabase(
            assertionMode = DatabaseAssertionMode.NON_STRICT,
            columnFilters = {
                    EntityIdExclusionFilter.class
            },
            value = "classpath:datasets/expected/bottles-create.xml", table = "BOTTLE")
    @Test
    public void testCreateBottle() throws Exception {
        Bottle bottle = new Bottle();
        bottle.setBrandName("Johny Walker");
        bottle.setPrice(new BigDecimal(289));
        bottle.setInShelf(true);
        bottle.setImageUrl("http://whiskey.last.jpg");
        bottle.setVolume(1.5);
        Beverage beverageRef = em.getReference(Beverage.class, 6);
        bottle.setBeverage(beverageRef);

        Bottle saved = bottleDao.create(bottle);
        em.flush();

        assertFalse(Strings.isNullOrEmpty(saved.getId()));
        assertEquals(8, countRowsInTable("BOTTLE"));
    }

    @ExpectedDatabase(value = "classpath:datasets/expected/bottles-update.xml", table = "BOTTLE")
    @Test
    public void testUpdateBottle() throws Exception {
        Bottle bottle = new Bottle();

        // update retrieved bottle
        Beverage beverage = new Beverage();
        beverage.setId(6);
        bottle.setId("bottle-000007");
        bottle.setBeverage(beverage);
        bottle.setBrandName("Johny Walker");
        bottle.setPrice(new BigDecimal(289));
        bottle.setVolume(1.5);
        bottle.setInShelf(true);
        bottle.setImageUrl("http://whiskey.last.jpg");

        // assert updated bottle
        Bottle updated = bottleDao.update(bottle);
        em.flush();

        assertEquals("Johny Walker", updated.getBrandName());
        assertEquals(updated.getBeverage().getId().intValue(), 6);
        assertTrue(updated.isInShelf());
        assertTrue(updated.getImageUrl().contains("whiskey"));
    }

    @ExpectedDatabase(value = "classpath:datasets/expected/bottles-delete.xml", table = "BOTTLE")
    @Test
    public void testDeleteBottle() throws Exception {
        bottleDao.delete("bottle-000001");
        em.flush();
    }

    private void assertLast(Bottle bottle) {
        assertEquals("bottle-000007", bottle.getId());
        assertEquals(3, bottle.getBeverage().getId().intValue());
        assertEquals("Havana Club", bottle.getBrandName());
        assertEquals(0.5, bottle.getVolume());
        assertThat(PRICE, Matchers.comparesEqualTo(bottle.getPrice()));
        assertFalse(bottle.isInShelf());
        assertTrue(bottle.getImageUrl().contains("rum"));
    }

}