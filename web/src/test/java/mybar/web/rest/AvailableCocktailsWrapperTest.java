package mybar.web.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import mybar.State;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.InsideBean;
import mybar.service.bar.ShelfService;
import mybar.web.rest.bar.AvailableCocktailsWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AvailableCocktailsWrapperTest {

    @Mock
    private ShelfService shelfService;

    private AvailableCocktailsWrapper cocktailsWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cocktailsWrapper = new AvailableCocktailsWrapper();
        cocktailsWrapper.setShelfService(shelfService);
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Integer) invocationOnMock.getArguments()[0] % 2 == 0; // not available = ingredient is missing
            }
        }).when(shelfService).isBottleAvailable(Matchers.anyInt());
    }

    @Test
    public void testGet() throws Exception {
        final CocktailBean emptyBean1 = new CocktailBean();
        emptyBean1.setId(1);
        emptyBean1.setInsideItems(ImmutableMap.<String, Collection<InsideBean>>of(IDrink.GROUP_NAME, Collections.<InsideBean>emptyList()));

        final CocktailBean emptyBean2 = new CocktailBean();
        emptyBean2.setId(2);
        emptyBean2.setInsideItems(ImmutableMap.<String, Collection<InsideBean>>of(IAdditive.GROUP_NAME, Collections.<InsideBean>emptyList()));

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
        assertThat(shot1.getInsideItems().get(IDrink.GROUP_NAME), is(empty()));
        CocktailBean shot2 = shots.get(1);
        assertEquals(State.UNDEFINED, shot2.getState());
        assertThat(shot2.getInsideItems().get(IAdditive.GROUP_NAME), is(empty()));

        List<CocktailBean> other = result.get("other");
        CocktailBean cocktail = other.get(0);
        Iterator<InsideBean> insideBeanIterator = cocktail.getInsideItems().get(IBeverage.GROUP_NAME).iterator();
        assertThat(insideBeanIterator.next().isMissing(), is(false));
        assertThat(insideBeanIterator.next().isMissing(), is(true));
        assertThat(insideBeanIterator.next().isMissing(), is(false));
        assertEquals(State.NOT_AVAILABLE, cocktail.getState());
    }

    public static CocktailBean addIngredients(CocktailBean cocktailBean) {
        InsideBean a = new InsideBean();
        a.setIngredientId(2);
        InsideBean b = new InsideBean();
        b.setIngredientId(3);
        InsideBean c = new InsideBean();
        c.setIngredientId(4);

        cocktailBean.setInsideItems(ImmutableMap.<String, Collection<InsideBean>>of(IBeverage.GROUP_NAME, Lists.newArrayList(a, b, c)));
        return cocktailBean;
    }

}