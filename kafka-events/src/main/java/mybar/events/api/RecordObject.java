package mybar.events.api;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class RecordObject<T> {
    public final long timestamp;
    public final T value;
}
