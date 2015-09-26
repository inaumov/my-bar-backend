package mybar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.OrderStatus;
import mybar.repository.history.OrderDao;
import mybar.domain.history.Order;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Transactional
    public List<Order> getOrderList(OrderStatus orderStatus) {
        List<Order> orders = orderDao.selectByOrdersType(orderStatus);
        return orders;
    }

    @Transactional
    public void prepared(Order o) {
        o.setOrderStatus(OrderStatus.PREPARED);
        orderDao.update(o);
    }

    @Transactional
    public void delivered(Order o) {
        o.setOrderStatus(OrderStatus.DELIVERED);
        orderDao.update(o);
    }

    public void notifyUser() {
        // TODO: implement event
    }

}