package mybar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "common.events"
        }
)
public class KafkaEventsTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaEventsTestApplication.class, args);
    }

}