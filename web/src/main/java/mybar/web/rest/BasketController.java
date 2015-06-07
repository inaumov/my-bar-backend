package mybar.web.rest;

import mybar.entity.Drink;
import mybar.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class BasketController {

    Logger logger = LoggerFactory.getLogger(BasketController.class);

    @Autowired
    private ClientService clientService;

    public void add(Drink drink) {
        clientService.addDrinkToBasket(drink);
    }

    public void remove(Drink drink) {
        clientService.removeDrinkFromBasket(drink);
    }

    public double getTotalPrice() {
        return clientService.getTotalPrice();
    }

    public int totalItems() {
        return clientService.totalItems();
    }

    public int count(Drink drink) {
        int cnt = 0;
        try {
            cnt = getDrinks().get(drink);
        } catch (NullPointerException e) {
            return cnt;
        }
        logger.info("Found drinks in basket = " + drink.getMenu().getName() + "|" + drink.getName() + " :: " + cnt);
        return cnt;
    }

    public double totalPerDrink(Drink drink) {
        double sum = 0;
        try {
            sum = count(drink) * drink.getPrice();
        } catch (NullPointerException e) {
            return sum;
        }
        return sum;
    }

    public Map<Drink, Integer> getDrinks() {
        Map<Drink, Integer> drinks = clientService.getDrinks();
        return drinks;
    }

    public String complete() {
        clientService.completeOrder();
        logger.info("Order completed");
        return "index.xhtml?faces-redirect=true";
    }

}