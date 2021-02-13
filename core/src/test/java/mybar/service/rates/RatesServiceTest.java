package mybar.service.rates;

import com.google.gson.Gson;
import mybar.api.rates.IRate;
import mybar.domain.bar.Cocktail;
import mybar.domain.rates.Rate;
import mybar.domain.users.User;
import mybar.dto.RateDto;
import mybar.messaging.IMessageProducer;
import mybar.repository.bar.CocktailDao;
import mybar.repository.rates.RatesDao;
import mybar.repository.users.UserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

@Disabled
@ExtendWith(MockitoExtension.class)
public class RatesServiceTest {

    public static final String USERNAME = "bill";
    public static final String COCKTAIL_ID = "cocktail-000099";
    public static final Integer STARS = 7;

    @Mock
    private UserDao userDaoMock;
    @Mock
    private RatesDao ratesDaoMock;
    @Mock
    private CocktailDao cocktailDaoMock;
    @Mock
    private IMessageProducer messageProducer;

    @InjectMocks
    private RatesService ratesService;

    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    public void test_rate() throws Exception {
        Mockito.when(cocktailDaoMock.read(COCKTAIL_ID)).thenReturn(new Cocktail());

        ratesService.rateCocktail(USERNAME, COCKTAIL_ID, STARS);
        ratesService.rateCocktail("Garry", COCKTAIL_ID, 8);
        ratesService.rateCocktail("Evan777", COCKTAIL_ID, 6);

        Map<String, IRate> rates = (Map<String, IRate>) ReflectionTestUtils.getField(ratesService, "tempRates");
        Assertions.assertNotNull(rates);
        Assertions.assertEquals(3, rates.size());
        Assertions.assertTrue(rates.containsKey(USERNAME + "@" + COCKTAIL_ID));
        Assertions.assertTrue(rates.containsKey("Garry" + "@" + COCKTAIL_ID));
        Assertions.assertTrue(rates.containsKey("Evan777" + "@" + COCKTAIL_ID));
        IRate iRate = ratesService.rateCocktail(USERNAME, COCKTAIL_ID, STARS);

        Assertions.assertEquals(COCKTAIL_ID, iRate.getCocktailId());
        Assertions.assertNotNull(iRate.getRatedAt());
        Assertions.assertEquals(STARS, iRate.getStars());

        Mockito.verify(messageProducer, Mockito.atLeastOnce()).send(Mockito.eq(USERNAME + "@" + COCKTAIL_ID), Mockito.anyString());
    }

    @Test
    public void test_rate_when_null_username() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(null, COCKTAIL_ID, STARS);
        });
    }

    @Test
    public void test_rate_when_missing_stars() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(USERNAME, COCKTAIL_ID, null);
        });
    }

    @Test
    public void test_rate_when_null_cocktail_id() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(USERNAME, null, STARS);
        });
    }

    @Test
    public void test_rate_when_stars_value_below_range() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(USERNAME, COCKTAIL_ID, 0);
        });
    }

    @Test
    public void test_rate_when_stars_value_above_range() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(USERNAME, COCKTAIL_ID, 11);
        });
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
        Mockito.when(userDaoMock.getOne(Mockito.anyString())).thenReturn(new User());

        User user = new User();
        user.setUsername(USERNAME);
        Cocktail cocktail = new Cocktail();
        cocktail.setId(COCKTAIL_ID);

        Rate rate = new Rate();
        rate.setUser(user);
        rate.setCocktail(cocktail);

        Mockito.when(ratesDaoMock.findAllRatesForUser(Mockito.any(User.class))).thenReturn(Collections.singletonList(rate));
        Collection<IRate> ratedCocktails = ratesService.getRatedCocktails(USERNAME);

        Assertions.assertEquals(ratedCocktails.size(), 1);
    }

    @Test
    public void test_persist_rates() throws Exception {
        TreeMap<String, IRate> testMap = new TreeMap<>();
        RateDto rateDto = new RateDto();
        rateDto.setRatedAt(new Date());
        rateDto.setStars(5);
        testMap.put(USERNAME + "@" + COCKTAIL_ID, rateDto);
        testMap.put(USERNAME + "@" + "cocktail-000002", rateDto);
        testMap.put(USERNAME + "@" + "cocktail-000350", rateDto);
        testMap.put("Garry" + "@" + COCKTAIL_ID, rateDto);
        testMap.put("Evan777" + "@" + COCKTAIL_ID, rateDto);

        ReflectionTestUtils.setField(ratesService, "tempRates", testMap);

        Gson gson = new Gson();
        for (String key : testMap.keySet()) {
            ratesService.persistRates(key, testMap.get(key).getRatedAt().getTime(), gson.toJson(testMap.get(key)));
        }
        // persist only when cocktail exists
        Mockito.verify(ratesDaoMock, Mockito.times(3)).update(Mockito.any(Rate.class));

        Assertions.assertTrue(testMap.isEmpty());
    }

}