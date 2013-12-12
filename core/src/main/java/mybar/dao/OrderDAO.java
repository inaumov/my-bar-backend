package mybar.dao;

import org.springframework.stereotype.Repository;
import mybar.OrderStatus;
import mybar.Report;
import mybar.api.IDish;
import mybar.entity.Order;

import javax.persistence.TypedQuery;
import java.sql.Date;
import java.util.List;

@Repository
public class OrderDAO extends GenericDaoImpl<Order> {

    public List<Order> selectByOrdersType(OrderStatus orderStatus) {
        List<Order> orders = null;

        switch (orderStatus) {
            case PREPARED:
            case DELIVERED:
            case NON_PREPARED: {
                TypedQuery<Order> q = em.createQuery("SELECT o FROM Order o WHERE o.isDone = :isDone ORDER BY o.sold", Order.class);
                q.setParameter("isDone", orderStatus);
                orders = q.getResultList();
                break;
            }
            case ALL: {
                TypedQuery<Order> q = em.createQuery("SELECT o FROM Order o ORDER BY o.sold", Order.class);
                orders = q.getResultList();
            }
        }
        return orders;
    }

    public boolean findDishInHistory(IDish d) {
        TypedQuery<Order> q = em.createQuery("SELECT o FROM Order o WHERE o.dish.id = :dish_id", Order.class);
        q.setParameter("dish_id", d.getId());
        q.setMaxResults(1);
        List<Order> orders = q.getResultList();
        if (!orders.isEmpty())
            return true;
        return false;
    }

    public List<Report> getReportForPeriod(Date startDate, Date endDate) {
        TypedQuery<Report> q = em.createQuery("SELECT new mybar.Report(d.name, o.amount) FROM Order o, Dish d WHERE o.dish.id = d.id AND o.sold >= :startDate AND o.sold <= :endDate", Report.class);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
        return q.getResultList();
    }

}