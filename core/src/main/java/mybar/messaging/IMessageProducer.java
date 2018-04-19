package mybar.messaging;

public interface IMessageProducer {
    void send(String key, String object);
}
