package mybar.events;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import mybar.dto.RateDto;
import mybar.events.api.RecordObject;
import mybar.events.impl.MyBarEventConsumer;
import mybar.service.rates.RatesService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class RatesEventConsumer extends MyBarEventConsumer<RateDto> {

    private final Splitter splitter = Splitter.on("@");

    private final Map<String, RecordObject<RateDto>> tempRates = new TreeMap<>();

    private final RatesService ratesService;

    public RatesEventConsumer(RatesService ratesService, String topic, String servers, String consumerGroupId, long pollTimeout) {
        super(topic, servers, consumerGroupId, pollTimeout);
        this.ratesService = ratesService;
    }

    @Override
    public void prepare(String key, RecordObject<RateDto> recordObject) {
        // consume all but keep the latest value
        tempRates.put(key, recordObject);
    }

    @Override
    public void consume() {
        for (String cacheKey : tempRates.keySet()) {
            RecordObject<RateDto> recordObject = tempRates.get(cacheKey);
            try {
                RateDto rateDto = recordObject.value;
                Iterable<String> keyStrings = splitter.split(cacheKey);
                Iterator<String> stringsIt = keyStrings.iterator();
                String userId = stringsIt.next();
                String cocktailId = stringsIt.next();
                LocalDateTime dateTime =
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(recordObject.timestamp), ZoneId.systemDefault());
                rateDto.setRatedAt(dateTime);
                rateDto.setCocktailId(cocktailId);

                ratesService.persistRate(userId, rateDto);
            } catch (Throwable throwable) {
                log.error("Could not persist rate for [{}].", cacheKey, throwable);
            }
        }
    }

}
