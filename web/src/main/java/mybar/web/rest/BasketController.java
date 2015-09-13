package mybar.web.rest;

import mybar.domain.Cocktail;
import mybar.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class BasketController {

    Logger logger = LoggerFactory.getLogger(BasketController.class);

    @Autowired
    private ClientService clientService;

    public void add(Cocktail cocktail) {
        clientService.addCocktailToBasket(cocktail);
    }

    public void remove(Cocktail cocktail) {
        clientService.removeCocktailFromBasket(cocktail);
    }

    public double getTotalPrice() {
        return clientService.getTotalPrice();
    }

    public int totalItems() {
        return clientService.totalItems();
    }

    public int count(Cocktail cocktail) {
        int cnt = 0;
        try {
            cnt = getCocktails().get(cocktail);
        } catch (NullPointerException e) {
            return cnt;
        }
        logger.info("Found cocktails in basket = " + cocktail.getMenu().getName() + "|" + cocktail.getName() + " :: " + cnt);
        return cnt;
    }

    public double totalPerCocktail(Cocktail cocktail) {
        double sum = 0;
        try {
            sum = count(cocktail) * cocktail.getPrice();
        } catch (NullPointerException e) {
            return sum;
        }
        return sum;
    }

    public Map<Cocktail, Integer> getCocktails() {
        Map<Cocktail, Integer> cocktails = clientService.getCocktails();
        return cocktails;
    }

    public String complete() {
        clientService.completeOrder();
        logger.info("Order completed");
        return "index.xhtml?faces-redirect=true";
    }

}