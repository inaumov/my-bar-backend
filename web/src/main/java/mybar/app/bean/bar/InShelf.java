package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum InShelf {
    YES, NO
}