package mybar.service;

import mybar.domain.bar.Cocktail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.OrderStatus;
import mybar.repository.history.OrderDao;
import mybar.repository.users.UserDao;
import mybar.domain.history.Order;

import java.util.*;

@Service
// TODO: obsolete logic - not for the first version.
public class ClientService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserDao user;

    private Map<Cocktail, Integer> basket;

    {
        Comparator<Cocktail> cmp = new Comparator<Cocktail>() {
            @Override
            public int compare(Cocktail o1, Cocktail o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        basket = new TreeMap<Cocktail, Integer>(cmp);
    }

    public void addCocktailToBasket(Cocktail cocktail) {
        Integer count = basket.get(cocktail);
        basket.put(cocktail, (count == null) ? 1 : count + 1);
    }

    public void removeCocktailFromBasket(Cocktail cocktail) {
        basket.remove(cocktail);
    }

    public Map<Cocktail, Integer> getCocktails() {
        return basket;
    }

    public double getTotalPrice() {
        double total = 0.0;
        Set<Map.Entry<Cocktail, Integer>> entries = basket.entrySet();
        for (Map.Entry entry : entries) {
            Cocktail cocktail = (Cocktail) entry.getKey();
            int cnt = entry.getValue().hashCode();
            total += cocktail.getPrice() * cnt;
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
        // todo: implement cocktails quantity
        Set<Cocktail> orders = basket.keySet();
        for (Cocktail cocktail : orders) {
            Order order = new Order();
            order.setCocktail(cocktail);
            order.setAmount(basket.get(cocktail));
            order.setOrderStatus(OrderStatus.NON_PREPARED);
            order.setUser(user.read(6));
            orderDao.create(order);
        }
        basket.clear();
    }

}