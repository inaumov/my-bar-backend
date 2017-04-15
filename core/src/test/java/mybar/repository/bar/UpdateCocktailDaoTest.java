package mybar.repository.bar;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.google.common.base.Predicate;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Iterables;
import mybar.UnitsValue;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.CocktailToIngredient;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.repository.GenericDaoImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.text.MessageFormat;
import java.util.List;

import static com.google.common.collect.DiscreteDomain.integers;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Range.closed;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests of sequential update a cocktail.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(
        {
                DependencyInjectionTestExecutionListener.class,
                DirtiesContextTestExecutionListener.class,
                DbUnitTestExecutionListener.class
        }
)
@ContextConfiguration(locations = "classpath:applicationContext-testUpdateCocktail-hsqldb.xml")
@DatabaseSetups(
        {
                @DatabaseSetup("classpath:dataSet.xml")
        }
)
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
public class UpdateCocktailDaoTest {

    private static EntityManagerFactory entityManagerFactory;

    public static final List<Integer> INGREDIENTS_TEST_IDS = newArrayList(ContiguousSet.create(closed(1, 18), integers()));

    @BeforeClass
    public static void init() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("cocktailUpdatePU");
    }

    private EntityManager entityManager;
    private EnhancedCocktailDao cocktailDao;
    private EnhancedIngredientDao ingredientDao;

    @Before
    public void setUp() throws Exception {
        entityManager = entityManagerFactory.createEntityManager();
        cocktailDao = new EnhancedCocktailDao();
        ingredientDao = new EnhancedIngredientDao();
        cocktailDao.setEntityManager(entityManager);
        ingredientDao.setEntityManager(entityManager);
    }

    @Test
    public void testCreateAndUpdateCocktail() throws Exception {

        entityManager.getTransaction().begin();

        Cocktail cocktail = new Cocktail();
        cocktail.setName("New Cocktail");
        cocktail.setImageUrl("http://img.path.jpg");
        cocktail.setDescription("some description");
        cocktail.setMenuId(3);

        cocktail = cocktailDao.create(cocktail);
        entityManager.getTransaction().commit();

        assertNotNull(cocktail);
        assertThat(cocktail.getId(), is(13));
        assertEquals("Menu ID related to cocktail should be same.", 3, cocktail.getMenuId());
        assertEquals("Cocktail name should same.", "New Cocktail", cocktail.getName());
        assertEquals("Cocktail description should be same.", "some description", cocktail.getDescription());
        assertEquals("Cocktail image url should be same.", "http://img.path.jpg", cocktail.getImageUrl());
        assertTrue("Ingredients list in cocktail should be empty.", cocktail.getCocktailToIngredientList().isEmpty());

        // check total cocktail to ingredient relations total rows before
        assertEquals("Number of CocktailToIngredient rows should be 17 before.", 17, getTotalCocktailToIngredientCount(entityManager));

        // force update
        List<Ingredient> all = ingredientDao.findAll();
        assertThat("Number of ingredients should be 18.", all, hasSize(18));

        // save previous values;
        int id = cocktail.getId();
        String name = cocktail.getName();
        String description = cocktail.getDescription();
        String imageUrl = cocktail.getImageUrl();
        int menuId = cocktail.getMenuId();

        for (int i = 0; i < all.size(); i++) {

            entityManager.getTransaction().begin();

            // refresh entity
            Cocktail cocktailForUpdate = cocktailDao.read(id);
            assertNotNull(cocktailForUpdate);
            assertThat(cocktailForUpdate.getId(), greaterThan(12));
            assertEquals("Cocktail name should same.", name, cocktailForUpdate.getName());
            assertEquals("Cocktail description should be same.", description, cocktailForUpdate.getDescription());
            assertEquals("Cocktail image url should be same.", imageUrl, cocktailForUpdate.getImageUrl());
            assertEquals("Menu ID related to cocktail should be same.", menuId, cocktailForUpdate.getMenuId());
            assertEquals("Ingredients list in cocktail should have same number of ingredients.", i, cocktailForUpdate.getCocktailToIngredientList().size());

            // save new values
            name = cocktail.getName() + i;
            description = cocktail.getDescription() + i;
            imageUrl = cocktail.getImageUrl() + i;
            menuId = cocktail.getMenuId();

            // set new values
            cocktailForUpdate.setId(id);
            cocktailForUpdate.setName(name);
            cocktailForUpdate.setDescription(description);
            cocktailForUpdate.setImageUrl(imageUrl);
            cocktailForUpdate.setMenuId(menuId);

            // add cocktail to ingredients relation
            CocktailToIngredient cocktailToIngredient = new CocktailToIngredient();
            cocktailToIngredient.setIngredient(all.get(i));
            cocktailToIngredient.setVolume(i * 10);
            cocktailForUpdate.addCocktailToIngredient(cocktailToIngredient);
            cocktailDao.update(cocktailForUpdate);

            entityManager.getTransaction().commit();

            // after each update iteration
            assertUpdatedCocktail(all.subList(0, i + 1), id, name, description, imageUrl, menuId);
        }

    }

    private void assertUpdatedCocktail(List<Ingredient> ingredients, int id, String name, String description, String imageUrl, int menuId) {
        // check saved cocktail
        Cocktail updatedCocktail = cocktailDao.read(id);
        assertNotNull(updatedCocktail);
        assertThat(updatedCocktail.getId(), is(13));
        assertEquals("Cocktail name should same.", name, updatedCocktail.getName());
        assertEquals("Cocktail description should be same.", description, updatedCocktail.getDescription());
        assertEquals("Cocktail image url should be same.", imageUrl, updatedCocktail.getImageUrl());
        assertEquals("Menu ID related to cocktail should be same.", menuId, updatedCocktail.getMenuId());
        // check cocktailToIngredientList related to new cocktail
        List<CocktailToIngredient> cocktailToIngredientList = updatedCocktail.getCocktailToIngredientList();
        int expectedCocktailToIngredientRelationsCnt = ingredients.size();
        assertEquals("Number of ingredients in cocktail should be same.", expectedCocktailToIngredientRelationsCnt, cocktailToIngredientList.size());

        for (int i = 0; i < expectedCocktailToIngredientRelationsCnt; i++) {
            Ingredient ingredient = ingredients.get(i);
            UnitsValue uv = ingredient instanceof Beverage || ingredient instanceof Drink ? UnitsValue.ML : UnitsValue.PCS;
            assertCocktailToIngredient(cocktailToIngredientList, ingredient.getKind(), ingredient.getId(), i * 10, uv, ingredient.getClass());
        }

        // check total cocktail to ingredient relations total rows after
        int addedCnt = expectedCocktailToIngredientRelationsCnt;
        int expectedTotal = 17 + addedCnt;
        int dbTotal = getTotalCocktailToIngredientCount(entityManager);
        assertEquals(MessageFormat.format("Number of CocktailToIngredient rows should be increased by {0} ingredients.", addedCnt), expectedTotal, dbTotal);
        // check that ingredients remain same
        assertThat("Ingredient list should remain the same.", findAllIngredientIds(entityManager), equalTo(INGREDIENTS_TEST_IDS));
    }

    private static void assertCocktailToIngredient(List<CocktailToIngredient> cocktailToIngredientList,
                                                   String ingredientName,
                                                   int ingredientId,
                                                   int expectedVolume,
                                                   UnitsValue unitsValue, Class type) {
        CocktailToIngredient cocktailToIngredient = findCocktailToIngredientByIngredientName(cocktailToIngredientList, ingredientName);
        assertEquals("Ingredient ID should be same.", ingredientId, cocktailToIngredient.getIngredient().getId());
        assertEquals("Volume of ingredient should be same.", expectedVolume, cocktailToIngredient.getVolume(), 0);
        assertEquals("Units value of ingredient should be same.", unitsValue, cocktailToIngredient.getUnitsValue());
        assertThat("Ingredient class type should be same.", cocktailToIngredient.getIngredient(), isA(type));
    }

    public static CocktailToIngredient findCocktailToIngredientByIngredientName(
            List<CocktailToIngredient> cocktailToIngredientList, final String ingredientName) {

        CocktailToIngredient cocktailToIngredient = Iterables.find(cocktailToIngredientList, new Predicate<CocktailToIngredient>() {
            @Override
            public boolean apply(CocktailToIngredient cocktailToIngredient) {
                return cocktailToIngredient.getIngredient().getKind().equals(ingredientName);
            }
        });
        assertNotNull(MessageFormat.format("Cocktail to ingredient should be present for ingredientName = {0}.", ingredientName), cocktailToIngredient);
        return cocktailToIngredient;
    }

    private int getTotalCocktailToIngredientCount(EntityManager em) {
        String sql = "SELECT count(cti) FROM CocktailToIngredient cti";
        Query q = em.createQuery(sql);
        Long count = (Long) q.getSingleResult();
        return count.intValue();
    }

    protected List<Integer> findAllIngredientIds(EntityManager em) {
        Query q = em.createQuery("SELECT i.id FROM Ingredient i");
        List<Integer> result = q.getResultList();
        return result;
    }

    private class EnhancedCocktailDao extends GenericDaoImpl<Cocktail> {
        public void setEntityManager(EntityManager em) {
            super.em = em;
        }
    }

    private class EnhancedIngredientDao extends IngredientDao {
        public void setEntityManager(EntityManager em) {
            super.em = em;
        }
    }

}