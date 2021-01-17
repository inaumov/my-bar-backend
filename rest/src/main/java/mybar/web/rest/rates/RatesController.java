package mybar.web.rest.rates;

import lombok.extern.slf4j.Slf4j;
import mybar.api.rates.IRate;
import mybar.app.bean.rates.RateBean;
import mybar.service.rates.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/rates")
@Secured("ROLE_USER")
public class RatesController {

    @Autowired
    private RatesService ratesService;

    @RequestMapping(value = "/average", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Double> findAllAverageRates() {
        return ratesService.findAllAverageRates();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RateBean rate(@RequestBody RateBean rateBean) {
        String principal = getPrincipalName();
        IRate rate = ratesService.rateCocktail(principal, rateBean.getCocktailId(), rateBean.getStars());
        return new RateBean(rate);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RateBean update(@RequestBody RateBean rateBean) {
        String principal = getPrincipalName();
        IRate rate = ratesService.rateCocktail(principal, rateBean.getCocktailId(), rateBean.getStars());
        return new RateBean(rate);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{cocktailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String cocktailId) {
        ratesService.removeCocktailFromRates(getPrincipalName(), cocktailId);
    }

    @RequestMapping(value = "/ratedCocktails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RateBean> getRates() {
        Collection<IRate> ratedCocktails = ratesService.getRatedCocktails(getPrincipalName());
        return ratedCocktails
                .stream()
                .map(RateBean::new)
                .collect(Collectors.toList());
    }

    private String getPrincipalName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}