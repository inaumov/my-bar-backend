package mybar.repository.bar;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Menu;
import mybar.repository.BaseDaoTest;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic Tests of Menu DAO.
 */
@DatabaseSetup("classpath:datasets/dataset.xml")
@ContextConfiguration(classes = {MenuDao.class, CocktailDao.class})
public class MenuDaoTest extends BaseDaoTest {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private CocktailDao cocktailDao;

    @Test
    @ExpectedDatabase(value = "classpath:datasets/dataset.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPreconditions() {
        // do nothing, just load and check dataSet and context loads
        assertThat(menuDao).isNotNull();
        assertThat(cocktailDao).isNotNull();
    }

    @Test
    public void testSelectAllMenus() {
        List<Menu> list = menuDao.findAll();
        assertEquals(3, list.size());
        Iterator<Menu> it = list.iterator();
        assertMenu(it.next(), 1, "shot");
        assertMenu(it.next(), 2, "long");
        assertMenu(it.next(), 3, "smoothie");
    }

    private void assertMenu(Menu c, int id, String name) {
        assertEquals(id, c.getId());
        assertEquals(name, c.getName());
    }

    @Test
    public void testMenuHasCocktails() {
        List<Menu> menuList = menuDao.findAll();
        Iterator<Menu> menuIterator = menuList.iterator();

        // test first menu
        Collection<Cocktail> cocktails = menuIterator.next().getCocktails();
        assertEquals(4, cocktails.size(), MessageFormat.format("Number of cocktails in the first [{0}] menu should be same.", menuList.get(0).getName()));
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), "cocktail-000001", 1, "B52");
        assertCocktail(cocktailIterator.next(), "cocktail-000002", 1, "B53");
        assertCocktail(cocktailIterator.next(), "cocktail-000003", 1, "Green Mexican");
        assertCocktail(cocktailIterator.next(), "cocktail-000004", 1, "Blow Job");

        // second menu
        cocktails = menuIterator.next().getCocktails();
        assertEquals(7, cocktails.size(), MessageFormat.format("Number of cocktails in the second [{0}] menu should be same.", menuList.get(1).getName()));

        // third menu
        cocktails = menuIterator.next().getCocktails();
        assertEquals(1, cocktails.size(), MessageFormat.format("Number of cocktails in the third [{0}] menu should be same.", menuList.get(2).getName()));
        cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), "cocktail-000012", 3, "Banana Blast II");
    }

    @Test
    public void testAddCocktailToMenu() {
        List<Menu> menuList = menuDao.findAll();

        Cocktail cocktail = new Cocktail();
        cocktail.setName("New Cocktail");
        Iterator<Menu> it = menuList.iterator();
        Menu firstMenu = it.next();
        cocktail.setMenuId(firstMenu.getId());
        cocktailDao.save(cocktail);

        menuList = menuDao.findAll();
        it = menuList.iterator();
        it.next();
        Collection<Cocktail> cocktails = cocktailDao.findByMenuId(firstMenu.getId());

        // test first menu
        assertEquals(5, cocktails.size(), MessageFormat.format("Number of cocktails in the first [{0}] menu should be same.", firstMenu.getName()));
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), "cocktail-000001", 1, "B52");
        assertCocktail(cocktailIterator.next(), "cocktail-000002", 1, "B53");
        assertCocktail(cocktailIterator.next(), "cocktail-000003", 1, "Green Mexican");
        assertCocktail(cocktailIterator.next(), "cocktail-000004", 1, "Blow Job");
        assertCocktail(cocktailIterator.next(), "cocktail-", 1, "New Cocktail");

        // second menu
        cocktails = it.next().getCocktails();
        assertEquals(7, cocktails.size(), MessageFormat.format("Number of cocktails in the second [{0}] menu should be same.", firstMenu.getName()));

        // third menu
        cocktails = it.next().getCocktails();
        assertEquals(1, cocktails.size(), MessageFormat.format("Number of cocktails in the third [{0}] menu should be same.", firstMenu.getName()));
        cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), "cocktail-", 3, "Banana Blast II");
    }

    public static void assertCocktail(Cocktail cocktail, String id, int menuId, String name) {
        Condition<String> existed = new Condition<>(
                s -> s.equals(id), "existed");
        Condition<String> added = new Condition<>(
                s -> s.startsWith(id), "added");
        assertThat(cocktail.getId())
                .withFailMessage("Cocktail ID should be same.")
                .is(anyOf(existed, added));
        assertThat(cocktail.getMenuId())
                .withFailMessage("Menu ID related to cocktail should be same.")
                .isEqualTo(menuId);
        assertThat(cocktail.getName())
                .withFailMessage("Cocktail name should same.")
                .isEqualTo(name);
    }

}