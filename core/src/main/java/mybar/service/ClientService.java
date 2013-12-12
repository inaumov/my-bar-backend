package mybar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.OrderStatus;
import mybar.dao.OrderDAO;
import mybar.dao.UserDAO;
import mybar.entity.Dish;
import mybar.entity.Order;

import java.util.*;

@Service
@Scope("session")
public class ClientService {

    @Autowired
    private OrderDAO orderDao;

    @Autowired
    private UserDAO user;

    private Map<Dish, Integer> cart;

    {
        Comparator<Dish> cmp = new Comparator<Dish>() {
            @Override
            public int compare(Dish o1, Dish o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        cart = new TreeMap<Dish, Integer>(cmp);
    }

    public void addDishToCart(Dish dish) {
        Integer count = cart.get(dish);
        cart.put(dish, (count == null) ? 1 : count + 1);
    }

    public void removeDishFromCart(Dish dish) {
        cart.remove(dish);
    }

    public Map<Dish, Integer> getDishes() {
        return cart;
    }

    public double getTotalPrice() {
        double total = 0.0;
        Set<Map.Entry<Dish, Integer>> entries = cart.entrySet();
        for (Map.Entry entry : entries) {
            Dish dish = (Dish) entry.getKey();
            int cnt = entry.getValue().hashCode();
            total += dish.getPrice() * cnt;
        }
        return total;
    }

    public int totalItems() {
        int cnt = 0;
        Collection<Integer> values = cart.values();
        for (Integer i : values) {
            cnt+=i;
        }
        return cnt;
    }

    @Transactional
    public void completeOrder() {
        // todo: implement dishes quantity
        Set<Dish> orders = cart.keySet();
        for (Dish dish : orders) {
            Order order = new Order();
            order.setDish(dish);
            order.setAmount(cart.get(dish));
            order.setOrderStatus(OrderStatus.NON_PREPARED);
            order.setUser(user.read(6));
            orderDao.create(order);
        }
        cart.clear();
    }

}