package mybar.events;

import mybar.events.api.KafkaMessageConsumer;
import mybar.events.api.RecordObject;
import mybar.service.rates.RatesService;

import java.util.Map;
import java.util.TreeMap;

public class RatesEventConsumer extends KafkaMessageConsumer {

    private final Map<String, RecordObject> tempRates = new TreeMap<>();

    private final RatesService ratesService;

    public RatesEventConsumer(RatesService ratesService, String topic, String servers, String consumerGroupId, long pollTimeout) {
        super(topic, servers, consumerGroupId, pollTimeout);
        this.ratesService = ratesService;
    }

    @Override
    public void aggregate(String key, RecordObject recordObject) {
        // consume all but keep the latest value
        tempRates.put(key, recordObject);
    }

    @Override
    public void consume() {
        for (String cacheKey : tempRates.keySet()) {
            RecordObject recordObject = tempRates.get(cacheKey);
            ratesService.persistRate(cacheKey, recordObject.timestamp, recordObject.value);
        }
    }

}
