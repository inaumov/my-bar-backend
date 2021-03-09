package mybar.service.rates;

import mybar.api.rates.IRate;
import mybar.events.api.IEventProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class RatesEventServiceTest {

    public static final String USERNAME = "bill";
    public static final String COCKTAIL_ID = "cocktail-000099";
    public static final Integer STARS = 7;

    @Mock
    private IEventProducer eventProducerMock;
    @Mock
    private RatesService ratesServiceMock;

    @InjectMocks
    private RatesEventService ratesEventService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void test_rate() {
        Mockito.when(eventProducerMock.send(Mockito.anyString(), Mockito.any(IRate.class))).thenReturn(Instant.now());
        Mockito.doNothing().when(ratesServiceMock).checkCocktailExists(COCKTAIL_ID);

        IRate iRate = ratesEventService.rateCocktail(USERNAME, COCKTAIL_ID, STARS);

        Assertions.assertEquals(COCKTAIL_ID, iRate.getCocktailId());
        Assertions.assertNotNull(iRate.getRatedAt());
        Assertions.assertEquals(STARS, iRate.getStars());

        Mockito.verify(eventProducerMock, Mockito.atLeastOnce()).send(Mockito.eq(USERNAME + "@" + COCKTAIL_ID), Mockito.any(IRate.class));
    }

    @Test
    public void test_rate_when_null_username() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesEventService.rateCocktail(null, COCKTAIL_ID, STARS);
        });
    }

    @Test
    public void test_rate_when_missing_stars() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesEventService.rateCocktail(USERNAME, COCKTAIL_ID, null);
        });
    }

    @Test
    public void test_rate_when_null_cocktail_id() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesEventService.rateCocktail(USERNAME, null, STARS);
        });
    }

    @Test
    public void test_rate_when_stars_value_below_range() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesEventService.rateCocktail(USERNAME, COCKTAIL_ID, 0);
        });
    }

    @Test
    public void test_rate_when_stars_value_above_range() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ratesEventService.rateCocktail(USERNAME, COCKTAIL_ID, 11);
        });
    }

}