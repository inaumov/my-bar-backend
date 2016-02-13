package mybar.dto.bar;

import mybar.domain.bar.Menu;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MenuTest {

    public static final int MENU_ID = 7;
    public static final String NAME = "Long drink";

    @Test
    public void testConvertCocktailToDto() throws Exception {
        Menu menu = new Menu();
        menu.setId(MENU_ID);
        menu.setName(NAME);

        MenuDto dto = menu.toDto();
        assertEquals(MENU_ID, dto.getId());
        assertEquals(NAME, dto.getName());
    }

}