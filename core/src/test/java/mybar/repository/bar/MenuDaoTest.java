package mybar.repository.bar;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import mybar.State;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Menu;
import mybar.repository.BaseDaoTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests Menu DAO.
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
        assertMenu(it.next(), 1, "Shot");
        assertMenu(it.next(), 2, "Long");
        assertMenu(it.next(), 3, "Smoothie");
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
        assertEquals(4, cocktails.size());
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), 1, 1, "B52", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 2, 1, "B53", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 3, 1, "Green Mexican", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 4, 1, "Blow Job", State.NOT_AVAILABLE);

        // second menu
        cocktails = menuIterator.next().getCocktails();
        assertEquals(7, cocktails.size());

        // third menu
        cocktails = menuIterator.next().getCocktails();
        assertEquals(1, cocktails.size());
        cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), 12, 3, "Banana Blast II", State.AVAILABLE);
    }

    @Test
    public void testAddCocktailToMenu() throws Exception {
        Menu firstMenu = menuDao.findAll().iterator().next();

        Cocktail cocktail = new Cocktail();
        cocktail.setName("New Cocktail");
        cocktail.setState(State.AVAILABLE);
        em.persist(cocktail);

        // Now persists the menu cocktail relationship
        firstMenu.addCocktail(cocktail);
        menuDao.update(firstMenu);

        System.out.println(cocktail.getMenu() + " with Cocktails:");
        System.out.println(cocktail.getMenu().getCocktails());

        List<Menu> menuList = menuDao.findAll();
        Iterator<Menu> it = menuList.iterator();

        // test first menu
        Collection<Cocktail> cocktails = it.next().getCocktails();
        assertEquals(5, cocktails.size());
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), 1, 1, "B52", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 2, 1, "B53", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 3, 1, "Green Mexican", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 4, 1, "Blow Job", State.NOT_AVAILABLE);
        assertCocktail(cocktailIterator.next(), 13, 1, "New Cocktail", State.AVAILABLE);

        // second menu
        cocktails = it.next().getCocktails();
        assertEquals(7, cocktails.size());

        // third menu
        cocktails = it.next().getCocktails();
        assertEquals(1, cocktails.size());
        cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), 12, 3, "Banana Blast II", State.AVAILABLE);
    }

    @Test
    public void testRemoveCocktailFromMenu() throws Exception {
        Menu lastMenu = menuDao.findAll().get(1);

        // Now persists the menu cocktail relationship
        Cocktail blackRussian = Iterables.find(lastMenu.getCocktails(), new Predicate<Cocktail>() {
            @Override
            public boolean apply(Cocktail cocktail) {
                return cocktail.getName().contains("Black Russian");
            }
        });
        cocktailDao.delete(blackRussian.getId());
        lastMenu.getCocktails().remove(blackRussian);
        menuDao.update(lastMenu);

        List<Menu> menuList = menuDao.findAll();
        Iterator<Menu> it = menuList.iterator();

        // test first menu
        Collection<Cocktail> cocktails = it.next().getCocktails();
        assertEquals(4, cocktails.size());
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        assertCocktail(cocktailIterator.next(), 1, 1, "B52", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 2, 1, "B53", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 3, 1, "Green Mexican", State.AVAILABLE);
        assertCocktail(cocktailIterator.next(), 4, 1, "Blow Job", State.NOT_AVAILABLE);

        // second menu
        cocktails = it.next().getCocktails();
        assertEquals(6, cocktails.size());

        // third menu
        cocktails = it.next().getCocktails();
        assertEquals(1, cocktails.size());
    }

    private void assertCocktail(Cocktail cocktail, int id, int menuId, String name, State status) {
        assertEquals(id, cocktail.getId());
        assertEquals(menuId, cocktail.getMenu().getId());
        assertEquals(name, cocktail.getName());
        assertEquals(status, cocktail.getState());
    }

}