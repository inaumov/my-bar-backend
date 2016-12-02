package mybar.dto.bar;

import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.IMenu;

@Getter
@Setter
public class MenuDto implements IMenu {

    private int id;
    private String name;
}