package mybar.web.rest.users;

import mybar.OrderStatus;
import mybar.domain.history.Order;
import mybar.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrderController {

    @Autowired
    private OrderService orderService;

    public List<Order> getOrders(OrderStatus orderStatus) {
        return orderService.getOrderList(orderStatus);
    }

    public void prepared(Order order) {
        orderService.prepared(order);
    }

    public void delivered(Order order) {
        orderService.delivered(order);
    }

}