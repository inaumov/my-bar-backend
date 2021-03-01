package mybar.events.api;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class RecordObject {
    public final long timestamp;
    public final String value;
}
