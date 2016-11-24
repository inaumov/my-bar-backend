package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;

public class MenuBean {

    @JsonView(View.Menu.class)
    private String name;

    @JsonView(View.Menu.class)
    private String translation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

}