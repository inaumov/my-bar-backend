package mybar.repository;

import mybar.OrderStatus;
import mybar.domain.Order;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import mybar.History;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OrderDaoTest extends BaseDaoTest {

    private static final String startDateStr = "2013-08-25";

    @Autowired
    private OrderDAO orderDao;

    @Test
    public void testGetHistoryForPeriod() throws Exception {
        Date startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(startDateStr);
        Date endDate = new Date(System.currentTimeMillis());

        List<History> result = orderDao.getHistoryForPeriod(getSqlDate(startDate), getSqlDate(endDate));
        assertFalse(result.isEmpty());

        Iterator<History> it = result.iterator();
        assertHistory(it.next(), "Blow Job", 5);
        assertHistory(it.next(), "Long Island Iced Tea", 2);
        assertHistory(it.next(), "Tequila Sunrise", 1);
    }

    private java.sql.Date getSqlDate(Date date) {
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        return sqlDate;
    }

    private void assertHistory(History r, String name, int amount) {
        assertEquals(name, r.getName());
        assertEquals(amount, r.getAmount());
    }

    private void assertHistory(Order o, int id, int drinkId, OrderStatus status, int amount) {

    }

}