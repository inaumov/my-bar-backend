package mybar.repository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test-hsqldb.xml")
@TestExecutionListeners(
        listeners = {
                DependencyInjectionTestExecutionListener.class,
                DirtiesContextTestExecutionListener.class,
                TransactionDbUnitTestExecutionListener.class
        },
        inheritListeners = false)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@DatabaseSetups(
        {
                @DatabaseSetup("classpath:dataSet.xml"),
//                @DatabaseSetup("classpath:umDataSet.xml"),
//                @DatabaseSetup("classpath:ordersDataSet.xml")
        }
)
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
public class BaseDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @PersistenceContext
    protected EntityManager em;

    @Test
    @ExpectedDatabase(value = "classpath:dataSet.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPreconditions() throws Exception {
        // do nothing, just load and check dataSet
    }

}