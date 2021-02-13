package mybar.repository.rates;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import mybar.History;
import mybar.domain.bar.Cocktail;
import mybar.domain.rates.Rate;
import mybar.domain.users.User;
import mybar.repository.BaseDaoTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate startDate = LocalDate.parse(START_DATE_STR, formatter);
        LocalDate endDate = LocalDate.now();

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
        Cocktail cocktail = new Cocktail();
        cocktail.setId("cocktail-000001");
        List<Rate> allRatesForCocktail = ratesDao.findAllRatesForCocktail(cocktail);
        assertEquals(2, allRatesForCocktail.size());
    }

    @Test
    public void testFindAllRatesForUser() throws Exception {
        User user = new User();
        user.setUsername("JohnDoe");
        List<Rate> allRatesForCocktail = ratesDao.findAllRatesForUser(user);
        assertEquals(5, allRatesForCocktail.size());
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

    @Test
    public void testFindAllAverageRates() throws Exception {
        Map<String, Double> allAverageRates = ratesDao.findAllAverageRates();
        assertEquals(5, allAverageRates.size());
        assertEquals(new Double(9.5), allAverageRates.get("cocktail-000001"));
    }

}