package mybar.app;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mybar.api.bar.IBottle;
import mybar.api.bar.ICocktail;
import mybar.api.bar.ICocktailIngredient;
import mybar.api.bar.IMenu;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.app.bean.bar.*;
import mybar.app.bean.bar.ingredient.AdditiveBean;
import mybar.app.bean.bar.ingredient.BeverageBean;
import mybar.app.bean.bar.ingredient.DrinkBean;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class RestBeanConverter {

    private static CocktailIngredientBean from(ICocktailIngredient inside) {
        CocktailIngredientBean bean = new CocktailIngredientBean();
        BeanUtils.copyProperties(inside, bean);
        return bean;
    }

    public static CocktailBean toCocktailBean(ICocktail cocktail) {
        CocktailBean bean = new CocktailBean();
        BeanUtils.copyProperties(cocktail, bean);
        bean.setIngredients(transformIngredients(cocktail.getIngredients()));
        return bean;
    }

    private static Map<String, Collection<CocktailIngredientBean>> transformIngredients(Map<String, ? extends Collection<? extends ICocktailIngredient>> map) {
        if (CollectionUtils.isEmpty(map)) {
            return Collections.emptyMap();
        }
        Map<String, Collection<CocktailIngredientBean>> transformedMap = Maps.newHashMap();
        for (Map.Entry<String, ? extends Collection<? extends ICocktailIngredient>> entry : map.entrySet()) {
            transformedMap.put(entry.getKey(), transformCocktailIngredients(entry.getValue()));
        }
        return transformedMap;
    }

    private static ArrayList<CocktailIngredientBean> transformCocktailIngredients(Collection<? extends ICocktailIngredient> cocktailIngredients) {
        ArrayList<CocktailIngredientBean> cocktailIngredientBeans = Lists.newArrayList();
        for (ICocktailIngredient cocktailIngredient : cocktailIngredients) {
            cocktailIngredientBeans.add(from(cocktailIngredient));
        }
        return cocktailIngredientBeans;
    }

    public static MenuBean toMenuBean(IMenu menu) {
        MenuBean bean = new MenuBean();
        bean.setName(menu.getName());
        return bean;
    }

    public static BottleBean from(IBottle bottle) {
        BottleBean bean = new BottleBean();
        BeanUtils.copyProperties(bottle, bean, "inShelf");
        bean.setInShelf(bottle.isInShelf() ? InShelf.YES : InShelf.NO);

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