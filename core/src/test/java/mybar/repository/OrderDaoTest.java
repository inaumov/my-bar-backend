package mybar.repository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import mybar.History;
import mybar.OrderStatus;
import mybar.domain.history.Order;
import mybar.repository.history.OrderDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:mybar/spring-test-config.xml")
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class},
        inheritListeners = false)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@DatabaseSetup("/HistoryData-set.xml")
public class OrderDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private OrderDao orderDao;

    private static final String startDateStr = "2013-08-25";

    protected final int ORDERS_CNT = 6;

    @Test
    @ExpectedDatabase(value = "/HistoryData-set.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testPreconditions() throws Exception {
        // do nothing, just load and check my-bar-test-data
    }

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

    private void assertHistory(Order o, int id, int cocktailId, OrderStatus status, int amount) {

    }

}