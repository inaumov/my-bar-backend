package mybar.dto.bar;

import mybar.domain.bar.Menu;
import mybar.dto.DtoFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MenuTest {

    public static final int MENU_ID = 7;
    public static final String NAME = "Long drink";

    @Test
    public void testConvertCocktailToDto() throws Exception {
        Menu menu = new Menu();
        menu.setId(MENU_ID);
        menu.setName(NAME);

        MenuDto dto = DtoFactory.toDto(menu);
        assertEquals(MENU_ID, dto.getId());
        assertEquals(NAME, dto.getName());
    }

}