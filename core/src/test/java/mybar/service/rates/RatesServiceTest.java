package mybar.service.rates;

import mybar.api.rates.IRate;
import mybar.domain.bar.Cocktail;
import mybar.domain.rates.Rate;
import mybar.domain.users.User;
import mybar.dto.RateDto;
import mybar.repository.bar.CocktailDao;
import mybar.repository.rates.RatesDao;
import mybar.repository.users.UserDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@RunWith(MockitoJUnitRunner.class)
public class RatesServiceTest {

    public static final String USERNAME = "bill";
    public static final String COCKTAIL_ID = "cocktail-000099";
    public static final int STARS = 7;

    @Mock
    private UserDao userDaoMock;
    @Mock
    private RatesDao ratesDaoMock;
    @Mock
    private CocktailDao cocktailDaoMock;

    @InjectMocks
    private RatesService ratesService;

    @Before
    public void setUp() throws Exception {
        Mockito.when(userDaoMock.findOne(Mockito.anyString())).thenReturn(new User());
        Mockito.when(cocktailDaoMock.read(COCKTAIL_ID)).thenReturn(new Cocktail());
    }

    @Test
    public void test_rate() throws Exception {
        ratesService.rateCocktail(USERNAME, COCKTAIL_ID, STARS);
        ratesService.rateCocktail("Garry", COCKTAIL_ID, 8);
        ratesService.rateCocktail("Evan777", COCKTAIL_ID, 6);
        Map<String, IRate> rates = (Map<String, IRate>) Whitebox.getInternalState(ratesService, "tempRates");
        Assert.assertNotNull(rates);
        Assert.assertEquals(3, rates.size());
        Assert.assertTrue(rates.containsKey(USERNAME + "@" + COCKTAIL_ID));
        Assert.assertTrue(rates.containsKey("Garry" + "@" + COCKTAIL_ID));
        Assert.assertTrue(rates.containsKey("Evan777" + "@" + COCKTAIL_ID));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_rate_when_null_username() throws Exception {
        ratesService.rateCocktail(null, COCKTAIL_ID, STARS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_rate_when_missing_stars() throws Exception {
        ratesService.rateCocktail(USERNAME, COCKTAIL_ID, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_rate_when_null_cocktail_id() throws Exception {
        ratesService.rateCocktail(USERNAME, null, STARS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_rate_when_stars_value_below_range() throws Exception {
        ratesService.rateCocktail(USERNAME, COCKTAIL_ID, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_rate_when_stars_value_above_range() throws Exception {
        ratesService.rateCocktail(USERNAME, COCKTAIL_ID, 11);
    }

    @Test
    public void test_remove_cocktail_from_rates() throws Exception {
        User user = new User();
        user.setUsername(USERNAME);
        Cocktail cocktail = new Cocktail();
        cocktail.setId(COCKTAIL_ID);

        Rate rate = new Rate();
        rate.setUser(user);
        rate.setCocktail(cocktail);

        Mockito.when(ratesDaoMock.findBy(USERNAME, COCKTAIL_ID)).thenReturn(rate);
        ratesService.removeCocktailFromRates(USERNAME, COCKTAIL_ID);
    }

    @Test
    public void test_get_my_rated_cocktails() throws Exception {
        User user = new User();
        user.setUsername(USERNAME);
        Cocktail cocktail = new Cocktail();
        cocktail.setId(COCKTAIL_ID);

        Rate rate = new Rate();
        rate.setUser(user);
        rate.setCocktail(cocktail);

        Mockito.when(ratesDaoMock.findAllRatesForUser(Mockito.any(User.class))).thenReturn(Collections.singletonList(rate));
        Collection<IRate> ratedCocktails = ratesService.getRatedCocktails(USERNAME);

        Assert.assertTrue(ratedCocktails.size() == 1);
    }

    @Test
    public void test_persist_rates() throws Exception {
        TreeMap<String, IRate> cacheMap = new TreeMap<>();
        RateDto rateDto = new RateDto();
        rateDto.setStars(5);
        cacheMap.put(USERNAME + "@" + COCKTAIL_ID, rateDto);
        cacheMap.put(USERNAME + "@" + "cocktail-000002", rateDto);
        cacheMap.put(USERNAME + "@" + "cocktail-000350", rateDto);
        cacheMap.put(USERNAME + "@" + "cocktail-000350", rateDto);
        cacheMap.put("Garry" + "@" + COCKTAIL_ID, rateDto);
        cacheMap.put("Evan777" + "@" + COCKTAIL_ID, rateDto);

        Whitebox.setInternalState(ratesService, "tempRates", cacheMap);

        ratesService.persistRates();

        Assert.assertTrue(cacheMap.isEmpty());
    }

}