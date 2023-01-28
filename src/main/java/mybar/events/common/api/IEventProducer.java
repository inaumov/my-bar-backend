package mybar.events.common.api;

import java.time.Instant;

public interface IEventProducer<T> {

    Instant send(String topicName, String userId, String entityId, T dto);
}