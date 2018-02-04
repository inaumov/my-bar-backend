package mybar.service.rates;

import mybar.api.rates.IRate;
import mybar.domain.bar.Cocktail;
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
        Map<String, IRate> rates = (Map<String, IRate>) Whitebox.getInternalState(ratesService, "rates");
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
        TreeMap<String, IRate> cacheMap = new TreeMap<>();
        cacheMap.put(USERNAME + "@" + COCKTAIL_ID, new RateDto());
        Whitebox.setInternalState(ratesService, "rates", cacheMap);
        ratesService.removeCocktailFromRates(USERNAME, COCKTAIL_ID);

        Assert.assertTrue(cacheMap.isEmpty());
    }

    @Test
    public void test_get_rated_cocktails() throws Exception {
        TreeMap<String, IRate> cacheMap = new TreeMap<>();
        cacheMap.put(USERNAME + "@" + COCKTAIL_ID, new RateDto());
        cacheMap.put(USERNAME + "@" + "cocktail-000002", new RateDto());
        cacheMap.put(USERNAME + "@" + "cocktail-000350", new RateDto());
        cacheMap.put(USERNAME + "@" + "cocktail-000350", new RateDto());
        cacheMap.put("Garry" + "@" + COCKTAIL_ID, new RateDto());
        cacheMap.put("Evan777" + "@" + COCKTAIL_ID, new RateDto());

        Whitebox.setInternalState(ratesService, "rates", cacheMap);
        Collection<IRate> ratedCocktails = ratesService.getRatedCocktails(USERNAME);

        Assert.assertTrue(ratedCocktails.size() == 3);
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

        Whitebox.setInternalState(ratesService, "rates", cacheMap);

        ratesService.persistRates();

        Assert.assertTrue(cacheMap.isEmpty());
    }

}