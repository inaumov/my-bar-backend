package mybar.web.rest;

import mybar.entity.Dish;
import mybar.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class BasketController {

    Logger logger = LoggerFactory.getLogger(BasketController.class);

    @Autowired
    private ClientService clientService;

    public void add(Dish dish) {
        clientService.addDishToCart(dish);
    }

    public void remove(Dish dish) {
        clientService.removeDishFromCart(dish);
    }

    public double getTotalPrice() {
        return clientService.getTotalPrice();
    }

    public int totalItems() {
        return clientService.totalItems();
    }

    public int count(Dish dish) {
        int cnt = 0;
        try {
            cnt = getDishes().get(dish);
        } catch (NullPointerException e) {
            return cnt;
        }
        logger.info("Found dishes in cart = " + dish.getCategory().getName() + "|" + dish.getName() + " :: " + cnt);
        return cnt;
    }

    public double totalPerDish(Dish dish) {
        double sum = 0;
        try {
            sum = count(dish) * dish.getPrice();
        } catch (NullPointerException e) {
            return sum;
        }
        return sum;
    }

    public Map<Dish, Integer> getDishes() {
        Map<Dish, Integer> dishes = clientService.getDishes();
        return dishes;
    }

    public String complete() {
        clientService.completeOrder();
        logger.info("Order completed");
        return "index.xhtml?faces-redirect=true";
    }

}