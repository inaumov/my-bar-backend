package mybar.repository.bar;

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
import static org.junit.Assert.assertNotNull;

public class MenuTest extends BaseDaoTest {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CocktailDao cocktailDao;

    @Test
    public void testSelectAllMenus() throws Exception {
        List<Menu> list = menuDao.findAll();
        assertEquals(3, list.size());
        Iterator<Menu> it = list.iterator();
        assertMenu(it.next(), 1, "Shooter");
        assertMenu(it.next(), 2, "Long");
        assertMenu(it.next(), 3, "Smoothie");
    }

    private void assertMenu(Menu c, int id, String name) {
        assertEquals(id, c.getId());
        assertEquals(name, c.getName());
    }

    @Test
    public void testGetCocktailsForMenu() throws Exception {
        List<Menu> list = menuDao.findAll();
        Iterator<Menu> it = list.iterator();

        // test first menu
        Collection<Cocktail> cocktails = it.next().getCocktails();
        assertEquals(4, cocktails.size());
        Iterator<Cocktail> cocktail = cocktails.iterator();
        assertCocktail(cocktail.next(), 1, 1, "B52", State.AVAILABLE);
        assertCocktail(cocktail.next(), 2, 1, "B53", State.AVAILABLE);
        assertCocktail(cocktail.next(), 3, 1, "Green Mexican", State.AVAILABLE);
        assertCocktail(cocktail.next(), 4, 1, "Blow Job", State.NOT_AVAILABLE);

        // second menu
        cocktails = it.next().getCocktails();
        assertEquals(7, cocktails.size());

        // third menu
        cocktails = it.next().getCocktails();
        assertEquals(1, cocktails.size());
        cocktail = cocktails.iterator();
        assertCocktail(cocktail.next(), 12, 3, "Banana Blast II", State.AVAILABLE);
    }

    private void assertCocktail(Cocktail cocktail, int id, int menuId, String name, State status) {
        assertEquals(id, cocktail.getId());
        assertEquals(menuId, cocktail.getMenu().getId());
        assertEquals(name, cocktail.getName());
        assertEquals(status, cocktail.getState());
    }

    @Test
    public void testSaveCocktail() throws Exception {
        Menu firstMenu = menuDao.findAll().iterator().next();
        for (int i = 99; i < 109; i++) {
            Cocktail cocktail = new Cocktail();
            cocktail.setMenu(firstMenu);
            cocktail.setName("Cocktail " + (char) i);
            cocktail.setState(State.AVAILABLE);
            cocktailDao.create(cocktail);

            // Now persists the menu cocktail relationship
            Collection<Cocktail> cocktails = firstMenu.getCocktails();
            if (cocktails != null)
                cocktails.add(cocktail);
            menuDao.update(firstMenu);
        }
        assertEquals(14, getAndAssertCocktails().size());
    }

    @Test
    public void testRemoveCocktailWhenNoSales() throws Exception {
        Menu firstMenu = menuDao.findAll().iterator().next();
        firstMenu.getCocktails().clear();
        menuDao.update(firstMenu);
    }

    @Test
    public void testThrowSalesExistExceptionWhenRemove() throws Exception {

    }

    @Test
    public void testEditCocktail() throws Exception {

    }

    @Test
    public void testGetBasisForCocktail() throws Exception {

    }

    protected Collection<Cocktail> getAndAssertCocktails() {
        Menu c = menuDao.findAll().iterator().next();
        Collection<Cocktail> cocktailList = c.getCocktails();
        assertCocktailList(cocktailList);
        return cocktailList;
    }

    protected void assertCocktailList(Collection<Cocktail> all) {
        Iterator<Cocktail> it = all.iterator();
        for (int id = 1; id <= all.size(); id++) {
            Cocktail cocktail = it.next();
            assertNotNull(cocktail.getId());
            assertNotNull(cocktail.getName());
            assertNotNull(cocktail.getState());
            assertEquals(1, cocktail.getMenu().getId());
        }
    }

}