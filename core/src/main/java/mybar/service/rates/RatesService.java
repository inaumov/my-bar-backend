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

import java.time.ZoneId;
import java.util.*;

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

            rateDto.setRatedAt(rateEntity.getRatedAt().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
            userRates.add(rateDto);
        }
        return Collections.unmodifiableCollection(userRates);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void persistRate(String userId, IRate iRate) {

        User user = userDao.getOne(userId);
        Cocktail cocktail = cocktailDao.read(iRate.getCocktailId());
        checkCocktailExists(iRate.getCocktailId());

        Rate rate = new Rate();
        rate.setCocktail(cocktail);
        rate.setStars(iRate.getStars());

        rate.setRatedAt(Date.from(iRate.getRatedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()));
        rate.setUser(user);
        ratesDao.update(rate);
    }

    void checkCocktailExists(String cocktailId) {
        Cocktail cocktail = cocktailDao.read(cocktailId);
        if (cocktail == null) {
            throw new CocktailNotFoundException(cocktailId);
        }
    }

    public Map<String, Double> findAllAverageRates() {
        return ratesDao.findAllAverageRates();
    }

}