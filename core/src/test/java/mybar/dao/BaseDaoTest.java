package mybar.dao;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import mybar.entity.um.Role;
import mybar.entity.um.User;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:mybar/spring-test-config.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class BaseDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    static final int COURIER_ID = 5;
    static final int CLIENT1_ID = 6; // with orders
    static final int CLIENT2_ID = 7; // to remove
    static final String CLIENT1_NAME = "client";
    static final String CLIENT2_NAME = "JohnDoe";

    @PersistenceContext
    protected EntityManager em;

    public static final String DATASET_XML = "dataset.xml";

    private IDataSet loadedDataSet;
    private IDatabaseTester databaseTester;

    protected final int ROLES_CNT = 6;
    protected final int USERS_CNT = 7;
    protected final int USER_HAS_ROLES_CNT = 8;

    protected final int CATEGORY_CNT = 4;
    protected final int DISH_CNT = 17;
    protected final int ORDERS_CNT = 6;

    @Before
    public void setUp() throws Exception {
        IDataSet dataSet = getDataSet();
        cleanlyInsert(dataSet);
        testPreconditions();
    }

    private void cleanlyInsert(IDataSet dataSet) throws Exception {
        DataSource ds = (DataSource) applicationContext.getBean("dataSource");
        databaseTester = new DataSourceDatabaseTester(ds);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    protected IDataSet getDataSet() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(DATASET_XML);
        loadedDataSet = new FlatXmlDataSetBuilder().build(stream);
        ReplacementDataSet dataSet = new ReplacementDataSet(loadedDataSet);
        dataSet.addReplacementObject("", "");
        return dataSet;
    }

    @After
    public void tearDown() throws Exception {
        databaseTester.onTearDown();
    }

    @Test
    public void testPreconditions() throws Exception {
        assertNotNull(loadedDataSet);
        int rolesCnt = loadedDataSet.getTable("roles").getRowCount();
        int usersCnt = loadedDataSet.getTable("users").getRowCount();
        int userRolesCnt = loadedDataSet.getTable("user_has_roles").getRowCount();
        int categoryCnt = loadedDataSet.getTable("category").getRowCount();
        int dishCnt = loadedDataSet.getTable("dish").getRowCount();
        int ordersCnt = loadedDataSet.getTable("orders").getRowCount();
        assertEquals(ROLES_CNT, rolesCnt);
        assertEquals(USERS_CNT, usersCnt);
        assertEquals(USER_HAS_ROLES_CNT, userRolesCnt);
        assertEquals(CATEGORY_CNT, categoryCnt);
        assertEquals(DISH_CNT, dishCnt);
        assertEquals(ORDERS_CNT, ordersCnt);
    }

    protected List<User> getAndAssertUsers() {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u", User.class);
        List<User> userList = q.getResultList();
        assertUsers(userList);
        return userList;
    }

    protected List<Role> getAndAssertRoles() {
        TypedQuery<Role> q = em.createQuery("SELECT r FROM Role r", Role.class);
        List<Role> roleList = q.getResultList();
        assertRoles(roleList);
        return roleList;
    }

    protected int countRelationsSize() {
        Query result = em.createNativeQuery("select count(ALL user_id) from user_has_roles");
        return (Integer) result.getSingleResult();
    }

    protected void assertUsers(List<User> all) {
        Iterator<User> it = all.iterator();
        for (int id = 1; id <= all.size(); id++) {
            assertEquals(id, it.next().getId());
        }
    }

    protected void assertRoles(List<Role> all) {
        Iterator<Role> it = all.iterator();
        for (int id = 1; id <= all.size(); id++) {
            assertEquals(id, it.next().getId());
        }
    }

}