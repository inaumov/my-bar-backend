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

/**
 * Tests Cocktail DAO.
 */
public class CocktailDaoTest extends BaseDaoTest {

    @Autowired
    private CocktailDao cocktailDao;


    @Test
    public void testRemoveCocktailWhenNoLikes() throws Exception {
        //Menu firstMenu = menuDao.findAll().iterator().next();
        //firstMenu.getCocktails().clear();
        //menuDao.update(firstMenu);
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
        //Menu c = menuDao.findAll().iterator().next();
        //Collection<Cocktail> cocktailList = c.getCocktails();
        //assertCocktailList(cocktailList);
        //return cocktailList;
        return null;
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