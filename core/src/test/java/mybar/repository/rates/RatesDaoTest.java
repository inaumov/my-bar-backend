package mybar.repository.rates;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import mybar.History;
import mybar.domain.bar.Cocktail;
import mybar.domain.rates.Rate;
import mybar.domain.users.User;
import mybar.dto.bar.CocktailDto;
import mybar.repository.BaseDaoTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@DatabaseSetup("classpath:datasets/dataset.xml")
@DatabaseSetup("classpath:datasets/usersDataSet.xml")
@DatabaseSetup("classpath:datasets/ratesDataSet.xml")
public class RatesDaoTest extends BaseDaoTest {

    @Autowired
    private RatesDao ratesDao;

    private static final String START_DATE_STR = "2013-08-25";
    private static final String NEW_DATE_STR = "2018-01-24";

    @Test
    @ExpectedDatabase(value = "classpath:datasets/ratesDataSet.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPreconditions() throws Exception {
        // do nothing, just load and check dataSet
    }

    @Test
    public void testGetHistoryForPeriod() throws Exception {
        Date startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(START_DATE_STR);
        Date endDate = new Date(System.currentTimeMillis());

        List<History> result = ratesDao.getRatedCocktailsForPeriod(startDate, endDate);
        assertFalse(result.isEmpty());

        Iterator<History> it = result.iterator();
        assertHistory(it.next(), "B52", 10);
        assertHistory(it.next(), "Blow Job", 5);
        assertHistory(it.next(), "Long Island Iced Tea", 7);
    }

    private void assertHistory(History history, String name, int stars) {
        assertEquals(name, history.getName());
        assertEquals(stars, history.getStars());
    }

    @Test
    public void testFindAllRatesForCocktail() throws Exception {
        CocktailDto cocktail = new CocktailDto();
        cocktail.setId("cocktail-000001");
        List<Rate> allRatesForCocktail = ratesDao.findAllRatesForCocktail(cocktail);
        assertEquals(2, allRatesForCocktail.size());
    }

    @ExpectedDatabase(
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
            value = "classpath:datasets/expected/rates-save.xml", table = "RATES")
    @Test
    public void testSaveRate() throws Exception {
        User user = em.getReference(User.class, "JohnDoe");
        Cocktail cocktail = em.getReference(Cocktail.class, "cocktail-000011");
        Date newDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(NEW_DATE_STR);

        Rate rate = new Rate();
        rate.setCocktail(cocktail);
        rate.setUser(user);
        rate.setStars(9);
        rate.setRatedAt(newDate);
        ratesDao.create(rate);

        em.flush();
    }

}