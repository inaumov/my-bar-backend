package mybar.repository.bar;

import mybar.State;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Menu;
import mybar.repository.BaseDaoTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Basic Tests of Menu DAO.
 */
public class MenuDaoTest extends BaseDaoTest {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private CocktailDao cocktailDao;

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
        assertExistedCocktail(cocktailIterator.next(), 1, 1, "B52", State.AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 2, 1, "B53", State.AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 3, 1, "Green Mexican", State.AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 4, 1, "Blow Job", State.NOT_AVAILABLE);

        // second menu
        cocktails = menuIterator.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the second [{0}] menu should be same.", menuList.get(1).getName()), 7, cocktails.size());

        // third menu
        cocktails = menuIterator.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the third [{0}] menu should be same.", menuList.get(2).getName()), 1, cocktails.size());
        cocktailIterator = cocktails.iterator();
        assertExistedCocktail(cocktailIterator.next(), 12, 3, "Banana Blast II", State.AVAILABLE);
    }

    @Test
    public void testAddCocktailToMenu() throws Exception {
        Menu firstMenu = menuDao.findAll().iterator().next();

        Cocktail cocktail = new Cocktail();
        cocktail.setName("New Cocktail");
        cocktail.setState(State.AVAILABLE);
        cocktailDao.create(cocktail);

        // Now persists the menu cocktail relationship
        firstMenu.addCocktail(cocktail);
        menuDao.update(firstMenu);

        List<Menu> menuList = menuDao.findAll();
        Iterator<Menu> it = menuList.iterator();

        // test first menu
        Collection<Cocktail> cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the first [{0}] menu should be same.", menuList.get(0).getName()), 5, cocktails.size());
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        assertExistedCocktail(cocktailIterator.next(), 1, 1, "B52", State.AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 2, 1, "B53", State.AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 3, 1, "Green Mexican", State.AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 4, 1, "Blow Job", State.NOT_AVAILABLE);
        assertExistedCocktail(cocktailIterator.next(), 13, 1, "New Cocktail", State.AVAILABLE);

        // second menu
        cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the second [{0}] menu should be same.", menuList.get(1).getName()), 7, cocktails.size());

        // third menu
        cocktails = it.next().getCocktails();
        assertEquals(MessageFormat.format("Number of cocktails in the third [{0}] menu should be same.", menuList.get(2).getName()), 1, cocktails.size());
        cocktailIterator = cocktails.iterator();
        assertExistedCocktail(cocktailIterator.next(), 12, 3, "Banana Blast II", State.AVAILABLE);
    }

    public static void assertExistedCocktail(Cocktail cocktail, int id, int menuId, String name, State state) {
        assertEquals("Cocktail ID should be same.", id, cocktail.getId());
        assertEquals("Menu ID related to cocktail should be same.", menuId, cocktail.getMenu().getId());
        assertEquals("Cocktail name should same.", name, cocktail.getName());
        assertEquals("Cocktail state should be same.", state, cocktail.getState());
    }

}