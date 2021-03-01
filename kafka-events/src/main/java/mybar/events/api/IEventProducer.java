package mybar.events.api;

public interface IEventProducer {
    Long send(String key, String object);
}
