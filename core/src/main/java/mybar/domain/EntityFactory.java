package mybar.domain;

import mybar.State;
import mybar.api.bar.*;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.history.IOrder;
import mybar.domain.bar.*;
import mybar.domain.bar.ingredient.Additive;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.domain.history.Order;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class EntityFactory {

    public static final Menu from(final IMenu menu) {
        Menu entity = new Menu();
        entity.setId(menu.getId());
        entity.setName(menu.getName());
        return entity;
    }

    public static final Cocktail from(final ICocktail cocktail) {
        Cocktail entity = new Cocktail();
        entity.setId(cocktail.getId());
        entity.setName(cocktail.getName());
        entity.setState(cocktail.getState());
        entity.setDescription(cocktail.getDescription());
        entity.setImageUrl(cocktail.getImageUrl());
        Map<String, ? extends Collection<? extends IInside>> insideItems = cocktail.getInsideItems();
        for (String groupName : insideItems.keySet()) {
            Collection<? extends IInside> iInsides = insideItems.get(groupName);
            for (IInside iInside : iInsides) {
                entity.addCocktailToIngredient(from(groupName, iInside));
            }
        }
        Menu menu = new Menu();
        menu.setId(cocktail.getId());
        entity.setMenu(menu);
        return entity;
    }

    public static final CocktailToIngredient from(String groupName, final IInside inside) {
        CocktailToIngredient cocktailToIngredient = new CocktailToIngredient();
        if (Objects.equals(groupName, IBeverage.GROUP_NAME)) {
            Beverage beverage = new Beverage();
            beverage.setId(inside.getIngredientId());
            cocktailToIngredient.setIngredient(beverage);
        } else if (Objects.equals(groupName, IDrink.GROUP_NAME)) {
            Drink drink = new Drink();
            drink.setId(inside.getIngredientId());
            cocktailToIngredient.setIngredient(drink);
        } else if (Objects.equals(groupName, IAdditive.GROUP_NAME)) {
            Additive additive = new Additive();
            additive.setId(inside.getIngredientId());
            cocktailToIngredient.setIngredient(additive);
        }
        cocktailToIngredient.setUnitsValue(inside.getUnitsValue());
        cocktailToIngredient.setVolume(inside.getVolume());
        return cocktailToIngredient;
    }

    public static final Ingredient from(final IBeverage beverage) {
        Beverage entity = new Beverage();
        entity.setId(beverage.getId());
        entity.setKind(beverage.getKind());
        entity.setBeverageType(beverage.getBeverageType());
        return entity;
    }

    public static final Ingredient from(final IDrink drink) {
        Drink entity = new Drink();
        entity.setId(drink.getId());
        entity.setKind(drink.getKind());
        entity.setDrinkType(drink.getDrinkType());
        return entity;
    }

    public static final Ingredient from(final IAdditive additive) {
        Additive entity = new Additive();
        entity.setId(additive.getId());
        entity.setKind(additive.getKind());
        return entity;
    }

    public static final Bottle from(final IBottle bottle) {
        Bottle entity = new Bottle();
        entity.setId(bottle.getId());
        entity.setVolume(bottle.getVolume());
        entity.setPrice(bottle.getPrice());
        entity.setBrandName(bottle.getBrandName());
        entity.setBeverage(new Beverage(bottle.getBeverage().getId()));
        entity.setState(bottle.isInShelf() ? State.AVAILABLE : State.NOT_AVAILABLE);
        entity.setImageUrl(bottle.getImageUrl());
        return entity;
    }

    public static final Order from(final IOrder order) {
        Order entity = new Order();
        entity.setId(order.getId());
        entity.setAmount(order.getAmount());
        entity.setSold(order.getSold());
        entity.setOrderStatus(order.getOrderStatus());
        return entity;
    }

}