package mybar.web.rest.bar;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import common.providers.availability.IAvailabilityCalculator;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.CocktailIngredientBean;
import mybar.app.bean.bar.YesNoEnum;
import mybar.service.bar.ShelfService;
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
public class CocktailAvailabilityCalculatorTest {

    @Autowired
    private ShelfService shelfService;
    @Autowired
    private IAvailabilityCalculator<CocktailBean> availabilityCalculator;

    @Before
    public void setUp() throws Exception {
        CocktailAvailabilityCalculator availabilityCalculator = new CocktailAvailabilityCalculator();
        availabilityCalculator.setShelfService(shelfService);
        this.availabilityCalculator = availabilityCalculator;
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
        emptyBean1.setId("cocktail-000001");
        emptyBean1.setIngredients(ImmutableMap.<String, Collection<CocktailIngredientBean>>of(IDrink.GROUP_NAME, Collections.<CocktailIngredientBean>emptyList()));

        final CocktailBean emptyBean2 = new CocktailBean();
        emptyBean2.setId("cocktail-000002");
        emptyBean2.setIngredients(ImmutableMap.<String, Collection<CocktailIngredientBean>>of(IAdditive.GROUP_NAME, Collections.<CocktailIngredientBean>emptyList()));

        CocktailBean testNotEmptyBean = new CocktailBean();
        testNotEmptyBean.setId("cocktail-000003");

        CocktailBean testAvailableBean = new CocktailBean();
        testNotEmptyBean.setId("cocktail-000004");

        ArrayList<CocktailBean> cocktails = Lists.newArrayList(emptyBean1, emptyBean2, addIngredients(testNotEmptyBean), prepareAvailableCocktail(testAvailableBean));
        for (CocktailBean cocktail : cocktails) {
            availabilityCalculator.doUpdate(cocktail);
        }
        assertEquals(4, cocktails.size());

        CocktailBean bean1 = cocktails.get(0);
        assertEquals(YesNoEnum.UNDEFINED, bean1.getHasAllIngredients());
        assertThat(bean1.getIngredients().get(IDrink.GROUP_NAME), is(empty()));

        CocktailBean bean2 = cocktails.get(1);
        assertEquals(YesNoEnum.UNDEFINED, bean2.getHasAllIngredients());
        assertThat(bean2.getIngredients().get(IAdditive.GROUP_NAME), is(empty()));

        CocktailBean bean3 = cocktails.get(2);
        Iterator<CocktailIngredientBean> ingredients3It = bean3.getIngredients().get(IBeverage.GROUP_NAME).iterator();
        assertThat(ingredients3It.next().isMissing(), is(false));
        assertThat(ingredients3It.next().isMissing(), is(true));
        assertThat(ingredients3It.next().isMissing(), is(false));
        assertEquals(YesNoEnum.NO, bean3.getHasAllIngredients());

        CocktailBean bean4 = cocktails.get(3);
        Iterator<CocktailIngredientBean> ingredients4It = bean4.getIngredients().get(IBeverage.GROUP_NAME).iterator();
        assertThat(ingredients4It.next().isMissing(), is(false));
        assertThat(ingredients4It.next().isMissing(), is(false));
        assertEquals(YesNoEnum.YES, bean4.getHasAllIngredients());
    }

    private CocktailBean prepareAvailableCocktail(CocktailBean cocktailBean) {
        CocktailIngredientBean a = new CocktailIngredientBean();
        a.setIngredientId(10);
        CocktailIngredientBean b = new CocktailIngredientBean();
        b.setIngredientId(20);

        cocktailBean.setIngredients(ImmutableMap.<String, Collection<CocktailIngredientBean>>of(IBeverage.GROUP_NAME, Lists.newArrayList(a, b)));
        return cocktailBean;
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