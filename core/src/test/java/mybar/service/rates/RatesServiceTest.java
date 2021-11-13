package mybar.service.rates;

import mybar.api.rates.IRate;
import mybar.domain.bar.Cocktail;
import mybar.domain.rates.Rate;
import mybar.domain.users.User;
import mybar.dto.RateDto;
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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RatesServiceTest {

    public static final String USERNAME = "bill";
    public static final String COCKTAIL_ID = "cocktail-000099";

    @Mock
    private UserDao userDaoMock;
    @Mock
    private RatesDao ratesDaoMock;
    @Mock
    private CocktailDao cocktailDaoMock;

    @InjectMocks
    private RatesService ratesService;

    @BeforeEach
    public void setUp() {
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
        rate.setRatedAt(LocalDateTime.now());

        Mockito.when(ratesDaoMock.findAllRatesForUser(Mockito.any(User.class))).thenReturn(Collections.singletonList(rate));
        Collection<IRate> ratedCocktails = ratesService.getRatedCocktails(USERNAME);

        Assertions.assertEquals(ratedCocktails.size(), 1);
    }

    @Test
    public void test_persist_rate() {
        Mockito.when(userDaoMock.getOne(Mockito.anyString())).thenReturn(new User());
        Mockito.when(cocktailDaoMock.findById(COCKTAIL_ID)).thenReturn(Optional.of(new Cocktail()));

        RateDto rateDto = new RateDto();
        rateDto.setCocktailId(COCKTAIL_ID);
        rateDto.setRatedAt(LocalDateTime.now());
        rateDto.setStars(5);

        ratesService.persistRate(USERNAME, rateDto);
        // persist only when cocktail exists
        Mockito.verify(ratesDaoMock, Mockito.atLeastOnce()).save(Mockito.any(Rate.class));
    }

}