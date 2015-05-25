package mybar.app.managedbean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mybar.OrderStatus;
import mybar.entity.Order;
import mybar.service.OrderService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;

@Component
@ManagedBean(name = "orderBean")
@SessionScoped
public class OrderBean {

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