package mybar.events.api;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class KafkaMessageConsumer implements MyBarEventConsumer {

    private final String TOPIC;
    private final String CONSUMER_GROUP_ID;
    private final String BOOTSTRAP_SERVERS;
    private final Duration POLL_TIMEOUT;

    @Setter
    public Consumer<String, String> consumer;

    public KafkaMessageConsumer(String topic, String servers, String consumerGroupId, long pollTimeout) {
        this.TOPIC = topic;
        this.CONSUMER_GROUP_ID = consumerGroupId;
        this.BOOTSTRAP_SERVERS = servers;
        this.POLL_TIMEOUT = Duration.ofMillis(pollTimeout);
    }

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

    void poll() {
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
                        log.debug("Consumed record: ({}, {}, {}, {})", record.key(), record.value(), record.partition(), record.offset());
                        aggregate(record.key(), RecordObject.of(record.timestamp(), record.value()));
                    });

                    consume();

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

}