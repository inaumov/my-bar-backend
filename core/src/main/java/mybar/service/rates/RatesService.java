package mybar.service.rates;

import lombok.extern.slf4j.Slf4j;
import mybar.api.rates.IRate;
import mybar.domain.bar.Cocktail;
import mybar.domain.rates.Rate;
import mybar.domain.users.User;
import mybar.dto.RateDto;
import mybar.exception.CocktailNotFoundException;
import mybar.repository.bar.CocktailDao;
import mybar.repository.rates.RatesDao;
import mybar.repository.users.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RatesService {

    private final RatesDao ratesDao;

    private final UserDao userDao;

    private final CocktailDao cocktailDao;

    @Autowired
    public RatesService(RatesDao ratesDao, UserDao userDao, CocktailDao cocktailDao) {
        this.ratesDao = ratesDao;
        this.userDao = userDao;
        this.cocktailDao = cocktailDao;
    }

    public void removeCocktailFromRates(String userId, String cocktailId) {
        Rate rate = ratesDao.findBy(userId, cocktailId);
        ratesDao.delete(rate);
    }

    public Collection<IRate> getRatedCocktails(String userId) {
        List<IRate> userRates = new ArrayList<>();
        User user = userDao.getOne(userId);
        List<Rate> allRatesForUser = ratesDao.findAllRatesForUser(user);
        for (Rate rateEntity : allRatesForUser) {
            RateDto rateDto = new RateDto();
            rateDto.setCocktailId(rateEntity.getCocktail().getId());
            rateDto.setStars(rateEntity.getStars());

            rateDto.setRatedAt(rateEntity.getRatedAt()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
            userRates.add(rateDto);
        }
        return Collections.unmodifiableCollection(userRates);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void persistRate(String userId, IRate iRate) {

        Cocktail cocktail = cocktailDao.findById(iRate.getCocktailId())
                .orElseThrow(() -> new CocktailNotFoundException(iRate.getCocktailId()));

        Rate rate = new Rate();
        rate.setCocktail(cocktail);
        rate.setStars(iRate.getStars());

        rate.setRatedAt(iRate.getRatedAt());
        User user = userDao.getOne(userId);
        rate.setUser(user);
        ratesDao.save(rate);
    }

    void checkCocktailExists(String cocktailId) {
        cocktailDao.findById(cocktailId)
                .orElseThrow(() -> new CocktailNotFoundException(cocktailId));
    }

    public Map<String, BigDecimal> findAllAverageRates() {
        List<Tuple> allAverageRates = ratesDao.findAllAverageRates();
        return allAverageRates.stream()
                .collect(Collectors.toMap(x -> x.get("cocktail_id", String.class), x -> BigDecimal.valueOf(x.get("avg_stars", Double.class))));
    }

}