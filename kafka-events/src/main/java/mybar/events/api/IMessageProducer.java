package mybar.events.api;

public interface IMessageProducer {
    Long send(String key, String object);
}
