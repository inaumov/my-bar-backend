package mybar.app.bean.bar;

import org.springframework.hateoas.ResourceSupport;
import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuBean extends ResourceSupport {

    private String name;
    private String translation;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("name", name)
                .add("translation", translation)
                .toString();
    }

}