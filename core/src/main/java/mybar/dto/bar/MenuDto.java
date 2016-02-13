package mybar.dto.bar;

import mybar.api.bar.IMenu;

public class MenuDto implements IMenu {

    private int id;
    private String name;

    @Override
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

}