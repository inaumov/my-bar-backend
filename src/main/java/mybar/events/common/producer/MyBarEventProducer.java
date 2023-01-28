package mybar.events.common.producer;

import lombok.extern.slf4j.Slf4j;
import mybar.events.common.api.IEventProducer;
import mybar.events.common.api.RecordObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;

@Slf4j
public class MyBarEventProducer<T> implements IEventProducer<T> {

    private final KafkaTemplate<String, RecordObject<?>> kafkaTemplate;

    public MyBarEventProducer(KafkaTemplate<String, RecordObject<?>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Instant send(String topicName, String userId, String entityId, T dto) {
        long time = System.currentTimeMillis();
        String key = asKey(userId, entityId);
        var recordObject = RecordObject.of(time, dto);
        var send = kafkaTemplate.send(topicName, 0, time, key, recordObject);
        send.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, RecordObject<?>> result) {
                var record = result.getProducerRecord();
                var metadata = result.getRecordMetadata();
                var timestamp = metadata.timestamp();
                long elapsedTime = timestamp - time;
                log.info("Event sent: record(key={}, value={}) meta(partition={}, offset={}) elapsed time={}.",
                        record.key(), record.value(), metadata.partition(),
                        metadata.offset(), elapsedTime);
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.error("Unable to send an event record for the key: {} due to : {}", key, throwable.getMessage());
            }
        });
        return Instant.ofEpochMilli(time);
    }

    private static String asKey(String userId, String entityId) {
        return userId + "@" + entityId;
    }

}