package mybar.events.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mybar.events.api.IEventConsumer;
import mybar.events.api.RecordObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class MyBarEventConsumer<T> implements IEventConsumer<T> {

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final String topic;
    private final String consumerGroupId;
    private final String bootstrapServers;
    private final Duration pollTimeout;

    @Setter
    public Consumer<String, T> consumer;

    public MyBarEventConsumer(String topic, String servers, String consumerGroupId, long pollTimeout) {
        this.topic = topic;
        this.consumerGroupId = consumerGroupId;
        this.bootstrapServers = servers;
        this.pollTimeout = Duration.ofMillis(pollTimeout);
    }

    private Consumer<String, T> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        // Create the consumer using props.
        return consumer == null ? new KafkaConsumer<>(props, keyDeserializer(), valueDeserializer()) : consumer;
    }

    private Deserializer<String> keyDeserializer() {
        return new StringDeserializer();
    }

    private Deserializer<T> valueDeserializer() {
        JsonDeserializer<T> valueDeserializer = new JsonDeserializer<>();
        valueDeserializer.addTrustedPackages("*");
        return valueDeserializer;
    }

    public void runConsumer() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(this::poll, 11, TimeUnit.SECONDS);
    }

    void poll() {
        try (final Consumer<String, T> consumer = createConsumer()) {
            // Subscribe to the topic.
            consumer.subscribe(Collections.singletonList(topic));

            while (!closed.get()) {
                final ConsumerRecords<String, T> records = consumer.poll(pollTimeout);
                // 10_000 is the time in milliseconds consumer will wait if no record is found at broker.
                if (records.count() == 0) {
                    log.warn("No records found. But continue.");
                    continue;
                }

                // print each record.
                records.forEach(record -> {
                    log.debug("Consumed record: ({}, {}, {}, {})",
                            record.key(),
                            record.value(),
                            record.partition(),
                            record.offset()
                    );
                    prepare(record.key(), RecordObject.of(record.timestamp(), record.value()));
                });
                // finish consuming
                consume();
                // commits the offset of record to broker.
                consumer.commitAsync();
            }
            log.info("Done consuming.");
        } catch (WakeupException e) {
            // Ignore exception if closing
            if (!closed.get()) throw e;
        } catch (Exception e) {
            log.error("Exception on Kafka Consumer polling... .", e);
        } finally {
            log.info("Closing Consumer automatically withing default TIMEOUT: {} ms.", 30000L);
        }
    }

    // Shutdown hook which is called from a separate thread
    public void shutdownAfter(Duration duration) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                closed.set(true);
                consumer.wakeup();
            }
        };
        timer.schedule(timerTask, duration.toMillis());
    }

}