package mybar.messaging.consumer;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mybar.service.rates.RatesService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KafkaMessageConsumer {

    private final String TOPIC;
    private final String CONSUMER_GROUP_ID;
    private final String BOOTSTRAP_SERVERS;
    private final long POLL_TIMEOUT;
    private final long CLOSE_TIMEOUT;

    @Setter
    public Consumer<String, String> consumer;

    @Autowired
    private RatesService ratesService;

    public KafkaMessageConsumer(String topic, String servers, String consumerGroupId, long pollTimeout, long closeTimeout) {
        this.TOPIC = topic;
        this.CONSUMER_GROUP_ID = consumerGroupId;
        this.BOOTSTRAP_SERVERS = servers;
        this.POLL_TIMEOUT = pollTimeout;
        this.CLOSE_TIMEOUT = closeTimeout;
    }

    private Map<String, RecordObject> tempRates = new TreeMap<>();

    private Consumer<String, String> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // Create the consumer using props.
        return consumer == null ? new KafkaConsumer<>(props) : consumer;
    }

    public void runConsumer() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::poll, 30, 360, TimeUnit.SECONDS);
    }

    private void poll() {
        try (final Consumer<String, String> consumer = createConsumer()) {
            // Subscribe to the topic.
            consumer.subscribe(Collections.singletonList(TOPIC));

            final int giveUp = 10;
            int noRecordsCount = 0;

            while (true) {
                try {
                    final ConsumerRecords<String, String> records = consumer.poll(POLL_TIMEOUT);
                    if (records.count() == 0) {
                        noRecordsCount++;
                        if (noRecordsCount > giveUp) {
                            log.warn("No records found. Break");
                            break;
                        } else {
                            log.warn("No records found. But continue.");
                            continue;
                        }
                    }

                    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>(records.partitions().size());

                    records.forEach(record -> {
                        System.out.printf("Consumer Record:(%s, %s, %d, %d)\n",
                                record.key(), record.value(),
                                record.partition(), record.offset());
                        // consume all but keep the latest value
                        tempRates.put(record.key(), RecordObject.of(record.timestamp(), record.value()));
                    });

                    for (String cacheKey : tempRates.keySet()) {
                        RecordObject recordObject = tempRates.get(cacheKey);
                        ratesService.persistRates(cacheKey, recordObject.timestamp, recordObject.value);
                    }
                    if (!offsets.isEmpty()) {
                        consumer.commitAsync(offsets, (map, exception) -> {
                            if (exception != null) {
                                log.error("Offset commit with offsets {} failed", offsets, exception);
                            }
                        });
                    }
                } catch (WakeupException e) {
                    // ignore for shutdown
                    // TODO: 4/18/2018
                } catch (Exception e) {
                    log.warn("Exception on Kafka Consumer polling... ", e);
                }
                log.info("Done consuming.");
            }
        } finally {
            log.info("Closing Consumer automatically withing default TIMEOUT: {} ms.", 30000L);
        }
    }

    @AllArgsConstructor(staticName = "of")
    private static class RecordObject {
        private long timestamp;
        private String value;
    }

}