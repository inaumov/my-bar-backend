package mybar.repository.bar;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Menu;
import mybar.repository.BaseDaoTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Basic Tests of Menu DAO.
 */
@DatabaseSetup("classpath:datasets/dataset.xml")
public class MenuDaoTest extends BaseDaoTest {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private CocktailDao cocktailDao;

    @Test
    @ExpectedDatabase(value = "classpath:datasets/dataset.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPreconditions() throws Exception {
        // do nothing, just load and check dataSet
    }

    @Test
    public void testSelectAllMenus() throws Exception {
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
    public void testMenuHasCocktails() throws Exception {
        List<Menu> menuList = menuDao.findAll();
        Iterator<Menu> menuIterator = menuList.iterator();

        // test first menu
        Collection<Cocktail> cocktails = menuIterator.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the first [{0}] menu should be same.", menuList.get(0).getName()), 4, cocktails.size());
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000001", 1, "B52");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000002", 1, "B53");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000003", 1, "Green Mexican");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000004", 1, "Blow Job");

        // second menu
        cocktails = menuIterator.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the second [{0}] menu should be same.", menuList.get(1).getName()), 7, cocktails.size());

        // third menu
        cocktails = menuIterator.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the third [{0}] menu should be same.", menuList.get(2).getName()), 1, cocktails.size());
        cocktailIterator = cocktails.iterator();
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000012", 3, "Banana Blast II");
    }

    @Test
    public void testAddCocktailToMenu() throws Exception {
        List<Menu> menuList = menuDao.findAll();

        Cocktail cocktail = new Cocktail();
        cocktail.setName("New Cocktail");
        Iterator<Menu> it = menuList.iterator();
        Menu firstMenu = it.next();
        cocktail.setMenuId(firstMenu.getId());
        cocktailDao.create(cocktail);

        menuList = menuDao.findAll();
        it = menuList.iterator();
        it.next();
        Collection<Cocktail> cocktails = cocktailDao.findAllFor(firstMenu);

        // test first menu
        assertEquals(MessageFormat.format("Number of cocktails in the first [{0}] menu should be same.", firstMenu.getName()), 5, cocktails.size());
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000001", 1, "B52");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000002", 1, "B53");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000003", 1, "Green Mexican");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-000004", 1, "Blow Job");
        assertExistedCocktail(cocktailIterator.next(), "cocktail-", 1, "New Cocktail");

        // second menu
        cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the second [{0}] menu should be same.", firstMenu.getName()), 7, cocktails.size());

        // third menu
        cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the third [{0}] menu should be same.", firstMenu.getName()), 1, cocktails.size());
        cocktailIterator = cocktails.iterator();
        assertExistedCocktail(cocktailIterator.next(), "cocktail-", 3, "Banana Blast II");
    }

    public static void assertExistedCocktail(Cocktail cocktail, String id, int menuId, String name) {
        assertThat("Cocktail ID should be same.", cocktail.getId(), either(equalTo(id)).or(containsString(id)));
        assertEquals("Menu ID related to cocktail should be same.", menuId, cocktail.getMenuId());
        assertEquals("Cocktail name should same.", name, cocktail.getName());
    }

}