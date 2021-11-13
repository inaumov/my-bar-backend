package mybar.repository.bar;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.base.Strings;
import mybar.api.bar.ingredient.BeverageType;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.repository.BaseDaoTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Bottle DAO.
 */
@DatabaseSetup("classpath:datasets/dataset.xml")
@ContextConfiguration(classes = BottleDao.class)
public class BottleDaoTest extends BaseDaoTest {

    private static final BigDecimal PRICE = new BigDecimal("119.00");
    @Autowired
    private BottleDao bottleDao;

    @Test
    @ExpectedDatabase(value = "classpath:datasets/dataset.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPreconditions() {
        // do nothing, just load and check dataSet and context loads
        assertThat(bottleDao).isNotNull();
    }

    @Test
    public void testFindAll() {
        List<Bottle> all = bottleDao.findAll();
        assertEquals(7, all.size());

        AtomicInteger id = new AtomicInteger();
        all.forEach(bottle -> assertEquals("bottle-00000" + id.incrementAndGet(), bottle.getId()));
    }

    @Test
    public void testDeleteAll() {
        bottleDao.deleteAll();
        commit();

        assertEquals(0, countRowsInTable("BOTTLE"));
    }

    @Test
    public void testReadById() {
        Bottle bottle = bottleDao.getOne("bottle-000007");

        assertLast(bottle);
    }

    @Test
    public void testGetBottlesByBeverage() {
        Bottle bottle = bottleDao.getOne("bottle-000003");

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
    public void testCreateBottle() {
        Bottle bottle = new Bottle();
        bottle.setBrandName("Johny Walker");
        bottle.setPrice(new BigDecimal(289));
        bottle.setInShelf(true);
        bottle.setImageUrl("http://whiskey.last.jpg");
        bottle.setVolume(1.5);
        Beverage beverageRef = testEntityManager.find(Beverage.class, 6);
        bottle.setBeverage(beverageRef);

        Bottle saved = bottleDao.save(bottle);
        commit();

        assertFalse(Strings.isNullOrEmpty(saved.getId()));
    }

    @ExpectedDatabase(value = "classpath:datasets/expected/bottles-update.xml", table = "BOTTLE")
    @Test
    public void testUpdateBottle() {
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
        Bottle updated = bottleDao.save(bottle);
        commit();

        assertEquals("Johny Walker", updated.getBrandName());
        assertEquals(updated.getBeverage().getId().intValue(), 6);
        assertTrue(updated.isInShelf());
        assertTrue(updated.getImageUrl().contains("whiskey"));
    }

    @ExpectedDatabase(value = "classpath:datasets/expected/bottles-delete.xml", table = "BOTTLE")
    @Test
    public void testDeleteBottle() {
        bottleDao.deleteById("bottle-000001");
        commit();
    }

    private void assertLast(Bottle bottle) {
        assertEquals("bottle-000007", bottle.getId());
        assertEquals(3, bottle.getBeverage().getId().intValue());
        assertEquals("Havana Club", bottle.getBrandName());
        assertEquals(0.5, bottle.getVolume());
        assertThat(PRICE).isEqualByComparingTo(bottle.getPrice());
        assertFalse(bottle.isInShelf());
        assertTrue(bottle.getImageUrl().contains("rum"));
    }

}