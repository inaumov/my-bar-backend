package mybar.events.api;

public interface MyBarEventConsumer {

    void runConsumer();

    void aggregate(String key, RecordObject recordObject);

    void consume();
}