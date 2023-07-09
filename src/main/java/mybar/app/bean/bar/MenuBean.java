package mybar.app.bean.bar;

import org.springframework.hateoas.RepresentationModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuBean extends RepresentationModel<MenuBean> {

    private String name;
    private String translation;

}