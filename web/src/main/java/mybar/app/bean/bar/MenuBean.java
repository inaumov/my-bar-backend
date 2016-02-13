package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.bar.IMenu;
import org.modelmapper.ModelMapper;

public class MenuBean implements IMenu {

    @JsonView(View.Menu.class)
    private int id;

    @JsonView(View.Menu.class)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static MenuBean from(IMenu menu) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(menu, MenuBean.class);
    }

}