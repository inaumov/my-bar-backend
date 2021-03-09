package mybar.events.api;

public interface IEventConsumer<T> {

    void runConsumer();

    void prepare(String key, RecordObject<T> recordObject);

    void consume();
}