package mybar.api.history;

import mybar.OrderStatus;

import java.util.Date;

public interface IOrder {

    int getId();

    Date getSold();

    OrderStatus getOrderStatus();

    int getAmount();

}