package mybar.events.api;

import java.time.Instant;

public interface IEventProducer {
    Instant send(String key, Object object);
}
