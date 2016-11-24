package mybar.app;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import mybar.api.bar.IBottle;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IInside;
import mybar.api.bar.IMenu;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.app.bean.bar.BottleBean;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.InsideBean;
import mybar.app.bean.bar.MenuBean;
import mybar.app.bean.bar.ingredient.AdditiveBean;
import mybar.app.bean.bar.ingredient.BeverageBean;
import mybar.app.bean.bar.ingredient.DrinkBean;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class RestBeanConverter {

    public static Function<IInside, InsideBean> toInsideBean = new Function<IInside, InsideBean>() {
        @Override
        public InsideBean apply(IInside inside) {
            return from(inside);
        }
    };

    private static InsideBean from(IInside inside) {
        InsideBean bean = new InsideBean();
        BeanUtils.copyProperties(inside, bean);
        return bean;
    }

    public static Function<Collection<? extends IInside>, Collection<InsideBean>> toInsideMap = new Function<Collection<? extends IInside>, Collection<InsideBean>>() {
        @Override
        public Collection<InsideBean> apply(Collection<? extends IInside> insideItems) {
            return FluentIterable.from(insideItems).transform(RestBeanConverter.toInsideBean).toList();
        }
    };

    public static CocktailBean toCocktailBean(ICocktail cocktail) {
        CocktailBean bean = new CocktailBean();
        BeanUtils.copyProperties(cocktail, bean);
        bean.setInsideItems(transformMap(cocktail.getInsideItems(), RestBeanConverter.toInsideMap)); // TODO remove functions
        return bean;
    }

    private static Map transformMap(Map<String, ? extends Collection<? extends IInside>> map, Function<Collection<? extends IInside>, Collection<InsideBean>> function) {
        if (CollectionUtils.isEmpty(map)) {
            return Collections.emptyMap();
        }
        Map<String, Collection<InsideBean>> transformedMap = Maps.newHashMap();
        for (Map.Entry<String, ? extends Collection<? extends IInside>> entry : map.entrySet()) {
            transformedMap.put(entry.getKey(), function.apply(entry.getValue()));
        }
        return transformedMap;
    }

    public static MenuBean toMenuBean(IMenu menu) {
        MenuBean bean = new MenuBean();
        bean.setName(menu.getName());
        return bean;
    }

    public static BottleBean from(IBottle bottle) {
        BottleBean bean = new BottleBean();
        BeanUtils.copyProperties(bottle, bean);

        IBeverage beverage = bottle.getBeverage();
        if (beverage != null) {
            bean.setBeverage(from(beverage));
        }
        return bean;
    }

    public static AdditiveBean from(IAdditive additive) {
        AdditiveBean bean = new AdditiveBean();
        copyIngredientFields(additive, bean);
        return bean;
    }

    public static BeverageBean from(IBeverage beverage) {
        BeverageBean bean = new BeverageBean();
        copyIngredientFields(beverage, bean);
        bean.setBeverageType(beverage.getBeverageType());
        return bean;
    }

    public static DrinkBean from(IDrink drink) {
        DrinkBean bean = new DrinkBean();
        copyIngredientFields(drink, bean);
        bean.setDrinkType(drink.getDrinkType());
        return bean;
    }

    private static void copyIngredientFields(IIngredient source, IIngredient targetBean) {
        BeanUtils.copyProperties(source, targetBean);
    }

}