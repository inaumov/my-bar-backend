package mybar.domain;

import mybar.OrderStatus;
import mybar.api.IOrder;
import mybar.domain.um.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "orders")
@SequenceGenerator(name = "ORDER_SEQUENCE", sequenceName = "ORDER_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Order implements IOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_SEQUENCE")
    private int id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "DRINK_ID")
    private Drink drink;

    @Temporal(TemporalType.DATE)
    private Date sold;

    @Column(name = "IS_DONE")
    @Enumerated(EnumType.STRING)
    private OrderStatus isDone;

    @Column(name = "AMOUNT")
    private int amount;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    @Override
    public Date getSold() {
        return sold;
    }

    public void setSold(Date sold) {
        this.sold = sold;
    }

    @Override
    public OrderStatus getOrderStatus() {
        return isDone;
    }

    public void setOrderStatus(OrderStatus isDone) {
        this.isDone = isDone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int getAmount() {
        return amount;
    }

}