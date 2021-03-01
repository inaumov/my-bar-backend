package mybar.events.api;

public interface IEventConsumer {

    void runConsumer();

    void aggregate(String key, RecordObject recordObject);

    void consume();
}