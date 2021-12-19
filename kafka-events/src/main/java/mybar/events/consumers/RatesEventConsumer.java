package mybar.events.consumers;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mybar.dto.RateDto;
import common.events.api.RecordObject;
import mybar.service.rates.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class RatesEventConsumer {

    private final Splitter splitter = Splitter.on("@");

    private final RatesService ratesService;

    @Getter
    private final CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    public RatesEventConsumer(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    @KafkaListener(topics = "${kafka.events.rates-topic-name}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "my-bar-backend")
    public void consume(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key, @Payload RecordObject<RateDto> recordObject) {
        log.info("Received payload='{}'", recordObject);
        try {
            RateDto rateDto = recordObject.getValue();
            Iterable<String> keyStrings = splitter.split(key);
            Iterator<String> stringsIt = keyStrings.iterator();
            String userId = stringsIt.next();
            String cocktailId = stringsIt.next();
            LocalDateTime dateTime =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(recordObject.timestamp), ZoneId.systemDefault());
            rateDto.setRatedAt(dateTime);
            rateDto.setCocktailId(cocktailId);

            ratesService.persistRate(userId, rateDto);
        } catch (Throwable throwable) {
            log.error("Could not persist rate for [{}].", key, throwable);
        } finally {
            latch.countDown();
        }
    }

}