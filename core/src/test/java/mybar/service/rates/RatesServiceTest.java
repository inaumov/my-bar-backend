package mybar.service.rates;

import com.google.gson.Gson;
import mybar.api.rates.IRate;
import mybar.domain.bar.Cocktail;
import mybar.domain.rates.Rate;
import mybar.domain.users.User;
import mybar.dto.RateDto;
import mybar.events.api.IEventProducer;
import mybar.repository.bar.CocktailDao;
import mybar.repository.rates.RatesDao;
import mybar.repository.users.UserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

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
    private IEventProducer messageProducer;

    @InjectMocks
    private RatesService ratesService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void test_rate() {
        Mockito.when(cocktailDaoMock.read(COCKTAIL_ID)).thenReturn(new Cocktail());

        IRate iRate = ratesService.rateCocktail(USERNAME, COCKTAIL_ID, STARS);

        Assertions.assertEquals(COCKTAIL_ID, iRate.getCocktailId());
        Assertions.assertNotNull(iRate.getRatedAt());
        Assertions.assertEquals(STARS, iRate.getStars());

        Mockito.verify(messageProducer, Mockito.atLeastOnce()).send(Mockito.eq(USERNAME + "@" + COCKTAIL_ID), Mockito.anyString());
    }

    @Test
    public void test_rate_when_null_username() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(null, COCKTAIL_ID, STARS);
        });
    }

    @Test
    public void test_rate_when_missing_stars() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(USERNAME, COCKTAIL_ID, null);
        });
    }

    @Test
    public void test_rate_when_null_cocktail_id() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(USERNAME, null, STARS);
        });
    }

    @Test
    public void test_rate_when_stars_value_below_range() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(USERNAME, COCKTAIL_ID, 0);
        });
    }

    @Test
    public void test_rate_when_stars_value_above_range() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesService.rateCocktail(USERNAME, COCKTAIL_ID, 11);
        });
    }

    @Test
    public void test_remove_cocktail_from_rates() {
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
    public void test_get_my_rated_cocktails() {
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
    public void test_persist_rate() {
        Mockito.when(userDaoMock.getOne(Mockito.anyString())).thenReturn(new User());
        Mockito.when(cocktailDaoMock.read(COCKTAIL_ID)).thenReturn(new Cocktail());

        Gson gson = new Gson();

        RateDto rateDto = new RateDto();
        rateDto.setRatedAt(new Date());
        rateDto.setStars(5);
        String key = USERNAME + "@" + COCKTAIL_ID;
        String object = gson.toJson(rateDto);

        ratesService.persistRate(key, System.currentTimeMillis(), object);
        // persist only when cocktail exists
        Mockito.verify(ratesDaoMock, Mockito.atLeastOnce()).update(Mockito.any(Rate.class));
    }

}