package mybar.events.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mybar.events.api.IEventProducer;
import mybar.events.serializer.MyBarJsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class MyBarEventProducer implements IEventProducer {

    private final String TOPIC;
    private final String BOOTSTRAP_SERVERS;
    private final String PRODUCER_CLIENT_ID;

    @Setter
    public Producer<String, Object> producer;

    public MyBarEventProducer(String topic, String servers, String producerClientId) {
        this.TOPIC = topic;
        this.PRODUCER_CLIENT_ID = producerClientId;
        this.BOOTSTRAP_SERVERS = servers;
    }

    private Producer<String, Object> createProducer() {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, PRODUCER_CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MyBarJsonSerializer.class.getName());

        return producer == null ? new KafkaProducer<>(props) : producer;
    }

    @Override
    public Instant send(final String key, final Object object) {
        producer = createProducer();
        long time = System.currentTimeMillis();
        try {
            final ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC, key, object);
            RecordMetadata metadata = producer
                    .send(record)
                    .get();
            long elapsedTime = metadata.timestamp() - time;
            log.debug("Record sent: record(key={}, value={}) meta(partition={}, offset={}) elapsed time={}.",
                    record.key(), record.value(), metadata.partition(),
                    metadata.offset(), elapsedTime);
            return Instant.ofEpochMilli(metadata.timestamp());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error in sending record: {} for the key: {}.", object, key, e);
        } finally {
            producer.flush(); // call producer.close() in production as well ??;
        }
        return null;
    }

}