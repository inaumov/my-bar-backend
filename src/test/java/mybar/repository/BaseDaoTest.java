package mybar.repository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.awaitility.Awaitility;
import org.dbunit.database.DefaultMetadataHandler;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.filter.IColumnFilter;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

@ContextConfiguration(
        classes = {
                BaseDaoTest.Config.class,
                BaseDaoTest.DbUnitConfig.class
        })
@TestExecutionListeners(
        listeners = {
                DependencyInjectionTestExecutionListener.class,
                DirtiesContextTestExecutionListener.class,
                DbUnitTestExecutionListener.class,
                TransactionalTestExecutionListener.class
        },
        inheritListeners = false)
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseDaoTest {
    protected final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Container
    private static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:14.2-alpine3.15")
            .withReuse(true)
            .withDatabaseName("my_bar")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        dbContainer.start();
    }

    @Autowired
    protected TestEntityManager testEntityManager;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate.setDataSource(dataSource);
    }

    public static class EntityIdExclusionFilter implements IColumnFilter {

        @Override
        public boolean accept(String tableName, Column column) {
            return !column.getColumnName().endsWith("ID");
        }
    }

    protected int countRowsInTable(String tableName) {
        return JdbcTestUtils.countRowsInTable(this.jdbcTemplate, tableName);
    }

    protected void commit() {
        testEntityManager.getEntityManager().getTransaction().commit();
    }

    @TestConfiguration
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackages = "mybar.repository")
    @EntityScan(basePackages = "mybar.domain")
    static class Config {

        @Bean
        @Primary
        public DataSource dataSource() {
            final DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(dbContainer.getJdbcUrl());
            dataSource.setUsername(dbContainer.getUsername());
            dataSource.setPassword(dbContainer.getPassword());
            dataSource.setSchema(dbContainer.getDatabaseName());

            Awaitility.await().pollInterval(Duration.ofSeconds(2L))
                    .atMost(Duration.ofMinutes(2L))
                    .until(() -> {
                        try (final Connection c = dataSource.getConnection()) {
                            c.prepareStatement(dbContainer.getTestQueryString());
                            return true;
                        } catch (SQLException e) {
                            return false;
                        }
                    });
            return dataSource;
        }
    }

    @TestConfiguration
    static class DbUnitConfig {

        private final DataSource dataSource;

        public DbUnitConfig(@Autowired DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
            var databaseDataSourceConnectionFactoryBean = new DatabaseDataSourceConnectionFactoryBean();
            databaseDataSourceConnectionFactoryBean.setDatabaseConfig(dbUnitDatabaseConfig());
            databaseDataSourceConnectionFactoryBean.setUsername(dbContainer.getUsername());
            databaseDataSourceConnectionFactoryBean.setPassword(dbContainer.getPassword());
            databaseDataSourceConnectionFactoryBean.setSchema(dbContainer.getDatabaseName());
            databaseDataSourceConnectionFactoryBean.setDataSource(dataSource);
            return databaseDataSourceConnectionFactoryBean;
        }

        private DatabaseConfigBean dbUnitDatabaseConfig() {
            DatabaseConfigBean configBean = new DatabaseConfigBean();
            configBean.setAllowEmptyFields(true);
            configBean.setDatatypeFactory(new PostgresqlDataTypeFactory());
            configBean.setCaseSensitiveTableNames(false);
            configBean.setMetadataHandler(new DefaultMetadataHandler());
            return configBean;
        }

    }

}