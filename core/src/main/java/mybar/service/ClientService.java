package mybar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.OrderStatus;
import mybar.dao.OrderDao;
import mybar.dao.UserDao;
import mybar.entity.Drink;
import mybar.entity.Order;

import java.util.*;

@Service
@Scope("session")
public class ClientService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserDao user;

    private Map<Drink, Integer> basket;

    {
        Comparator<Drink> cmp = new Comparator<Drink>() {
            @Override
            public int compare(Drink o1, Drink o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        basket = new TreeMap<Drink, Integer>(cmp);
    }

    public void addDrinkToBasket(Drink drink) {
        Integer count = basket.get(drink);
        basket.put(drink, (count == null) ? 1 : count + 1);
    }

    public void removeDrinkFromBasket(Drink drink) {
        basket.remove(drink);
    }

    public Map<Drink, Integer> getDrinks() {
        return basket;
    }

    public double getTotalPrice() {
        double total = 0.0;
        Set<Map.Entry<Drink, Integer>> entries = basket.entrySet();
        for (Map.Entry entry : entries) {
            Drink drink = (Drink) entry.getKey();
            int cnt = entry.getValue().hashCode();
            total += drink.getPrice() * cnt;
        }
        return total;
    }

    public int totalItems() {
        int cnt = 0;
        Collection<Integer> values = basket.values();
        for (Integer i : values) {
            cnt+=i;
        }
        return cnt;
    }

    @Transactional
    public void completeOrder() {
        // todo: implement drinks quantity
        Set<Drink> orders = basket.keySet();
        for (Drink drink : orders) {
            Order order = new Order();
            order.setDrink(drink);
            order.setAmount(basket.get(drink));
            order.setOrderStatus(OrderStatus.NON_PREPARED);
            order.setUser(user.read(6));
            orderDao.create(order);
        }
        basket.clear();
    }

}