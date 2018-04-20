package mybar.messaging;

public interface IMessageProducer {
    Long send(String key, String object);
}
