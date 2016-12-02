package mybar.web.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import mybar.State;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.CocktailIngredientBean;
import mybar.service.bar.ShelfService;
import mybar.web.rest.bar.AvailableCocktailsWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.reset;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("test-rest-context.xml")
public class AvailableCocktailsWrapperTest {

    @Autowired
    private ShelfService shelfService;

    private AvailableCocktailsWrapper cocktailsWrapper;

    @Before
    public void setUp() throws Exception {
        cocktailsWrapper = new AvailableCocktailsWrapper(shelfService);
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Integer) invocationOnMock.getArguments()[0] % 2 == 0; // not available = ingredient is missing
            }
        }).when(shelfService).isBottleAvailable(Matchers.anyInt());
    }

    @After
    public void tearDown() throws Exception {
        reset(shelfService);
    }

    @Test
    public void testGet() throws Exception {
        final CocktailBean emptyBean1 = new CocktailBean();
        emptyBean1.setId(1);
        emptyBean1.setIngredients(ImmutableMap.<String, Collection<CocktailIngredientBean>>of(IDrink.GROUP_NAME, Collections.<CocktailIngredientBean>emptyList()));

        final CocktailBean emptyBean2 = new CocktailBean();
        emptyBean2.setId(2);
        emptyBean2.setIngredients(ImmutableMap.<String, Collection<CocktailIngredientBean>>of(IAdditive.GROUP_NAME, Collections.<CocktailIngredientBean>emptyList()));

        CocktailBean testNotEmptyBean = new CocktailBean();
        testNotEmptyBean.setId(3);
        ImmutableMap<String, List<CocktailBean>> cocktails = ImmutableMap.<String, List<CocktailBean>>of(
                "shot", Lists.newArrayList(emptyBean1, emptyBean2),
                "other", Lists.newArrayList(addIngredients(testNotEmptyBean))
        );

        Map<String, List<CocktailBean>> result = cocktailsWrapper.get(cocktails);
        assertEquals(2, result.size());

        List<CocktailBean> shots = result.get("shot");
        CocktailBean shot1 = shots.get(0);
        assertEquals(State.UNDEFINED, shot1.getState());
        assertThat(shot1.getIngredients().get(IDrink.GROUP_NAME), is(empty()));
        CocktailBean shot2 = shots.get(1);
        assertEquals(State.UNDEFINED, shot2.getState());
        assertThat(shot2.getIngredients().get(IAdditive.GROUP_NAME), is(empty()));

        List<CocktailBean> other = result.get("other");
        CocktailBean cocktail = other.get(0);
        Iterator<CocktailIngredientBean> insideBeanIterator = cocktail.getIngredients().get(IBeverage.GROUP_NAME).iterator();
        assertThat(insideBeanIterator.next().isMissing(), is(false));
        assertThat(insideBeanIterator.next().isMissing(), is(true));
        assertThat(insideBeanIterator.next().isMissing(), is(false));
        assertEquals(State.NOT_AVAILABLE, cocktail.getState());
    }

    public static CocktailBean addIngredients(CocktailBean cocktailBean) {
        CocktailIngredientBean a = new CocktailIngredientBean();
        a.setIngredientId(2);
        CocktailIngredientBean b = new CocktailIngredientBean();
        b.setIngredientId(3);
        CocktailIngredientBean c = new CocktailIngredientBean();
        c.setIngredientId(4);

        cocktailBean.setIngredients(ImmutableMap.<String, Collection<CocktailIngredientBean>>of(IBeverage.GROUP_NAME, Lists.newArrayList(a, b, c)));
        return cocktailBean;
    }

}