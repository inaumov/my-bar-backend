package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuBean {

    @JsonView(View.Menu.class)
    private String name;

    @JsonView(View.Menu.class)
    private String translation;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("name", name)
                .add("translation", translation)
                .toString();
    }

}