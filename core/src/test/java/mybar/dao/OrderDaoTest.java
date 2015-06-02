package mybar.dao;

import mybar.OrderStatus;
import mybar.entity.Order;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import mybar.Report;

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
    private OrderDAO orderDAO;

    @Test
    public void testGetReportForPeriod() throws Exception {
        Date startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(startDateStr);
        Date endDate = new Date(System.currentTimeMillis());

        List<Report> result = orderDAO.getReportForPeriod(getSqlDate(startDate), getSqlDate(endDate));
        assertFalse(result.isEmpty());

        Iterator<Report> it = result.iterator();
        assertReportUnit(it.next(), "Blow Job", 5);
        assertReportUnit(it.next(), "Long Island Iced Tea", 2);
        assertReportUnit(it.next(), "Tequila Sunrise", 1);
    }

    private java.sql.Date getSqlDate(Date date) {
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        return sqlDate;
    }

    private void assertReportUnit(Report r, String name, int amount) {
        assertEquals(name, r.getName());
        assertEquals(amount, r.getAmount());
    }

    private void assertOrder(Order o, int id, int drinkId, OrderStatus status, int amount) {

    }

}