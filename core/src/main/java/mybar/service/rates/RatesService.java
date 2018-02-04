package mybar.service.rates;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
public class RatesService {

    @Autowired(required = false)
    private RatesDao ratesDao;

    @Autowired(required = false)
    private UserDao userDao;

    @Autowired(required = false)
    private CocktailDao cocktailDao;

    private static final Range<Integer> starsRange = new Range<>(1, 10);

    private Map<String, IRate> rates = new TreeMap<>();

    public IRate rateCocktail(String username, String cocktailId, Integer stars) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(username), "Username is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cocktailId), "Cocktail id is required.");
        Preconditions.checkArgument(stars != null && starsRange.contains(stars), "Stars number should be from 1 to 10.");
        checkCocktailExists(cocktailId);

        RateDto rate = new RateDto();
        rate.setCocktailId(cocktailId);
        rate.setRatedAt(new Date());
        rate.setStars(stars);
        rates.put(toCacheKey(username, cocktailId), rate);

        return rate;
    }

    public void removeCocktailFromRates(String userId, String cocktailId) {
        rates.remove(toCacheKey(userId, cocktailId));
    }

    private String toCacheKey(String userId, String cocktailId) {
        return userId + "@" + cocktailId;
    }

    public Collection<IRate> getRatedCocktails(String userId) {
        List<IRate> userRates = new ArrayList<>();
        for (String key : rates.keySet()) {
            if (key.startsWith(userId)) {
                userRates.add(rates.get(key));
            }
        }
        return Collections.unmodifiableCollection(userRates);
    }

    @Transactional
    public void persistRates() {
        Set<String> orders = rates.keySet();
        for (String cacheKey : orders) {
            IRate rateDto = rates.get(cacheKey);
            String[] strings = StringUtils.split(cacheKey, "@");
            User user = userDao.findOne(strings[0]);
            Cocktail cocktail = cocktailDao.read(strings[1]);
            if (user != null && cocktail != null) {
                Rate rate = new Rate();
                rate.setCocktail(cocktail);
                rate.setStars(rateDto.getStars());
                rate.setRatedAt(rateDto.getRatedAt());
                rate.setUser(user);
                ratesDao.create(rate);
            } else {
                log.error("Could not persist rate for [{}]. It is either user or cocktail is unknown.", cacheKey);
            }
        }
        rates.clear();
    }

    private void checkCocktailExists(String cocktailId) {
        Cocktail cocktail = cocktailDao.read(cocktailId);
        if (cocktail == null) {
            throw new CocktailNotFoundException(cocktailId);
        }
    }

}