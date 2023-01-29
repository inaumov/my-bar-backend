package mybar.context;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

@ContextConfiguration(initializers = {
        AbstractIntegrationTest.Initializer.class
})
public class AbstractIntegrationTest {

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.2-alpine3.15")
                .withReuse(true)
                .withDatabaseName("my_bar")
                .withUsername("postgres")
                .withPassword("postgres");

        private static void startContainers() {
            Startables
                    .deepStart(Stream.of(postgres))
                    .join();
        }

        private static Map<String, Object> createConnectionConfiguration() {
            return Map.of(
                    "spring.datasource.url", postgres.getJdbcUrl(),
                    "spring.datasource.username", postgres.getUsername(),
                    "spring.datasource.password", postgres.getPassword()
            );
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MapPropertySource testcontainers = new MapPropertySource(
                    "testcontainers", createConnectionConfiguration()
            );
            environment.getPropertySources().addFirst(testcontainers);
        }
    }

}
