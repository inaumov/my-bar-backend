package mybar.domain;

import mybar.State;
import mybar.api.bar.*;
import mybar.api.history.IOrder;
import mybar.domain.bar.*;
import mybar.domain.history.Order;

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
        entity.setDescription(cocktail.getDescription());
        for (IInside ingredient : cocktail.getIngredients()) {
            entity.getIngredients().add(from(ingredient));
        }
        entity.setState(cocktail.getState());
        //entity.setMenu(from(cocktail.getMenu()));
        return entity;
    }

    public static final Inside from(final IInside inside) {
        Inside entity = new Inside();
        entity.setId(inside.getId());
        entity.setUnitsValue(inside.getUnitsValue());
        entity.setVolume(inside.getVolume());
        IIngredient ingredient = inside.getIngredient();
        if (ingredient instanceof IBeverage) {
            entity.setIngredient(from((IBeverage) ingredient));
        } else if (ingredient instanceof IDrink) {
            entity.setIngredient(from((IDrink) ingredient));
        } else if (ingredient instanceof IAdditional) {
            entity.setIngredient(from((IAdditional) ingredient));
        }
        return entity;
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

    public static final Ingredient from(final IAdditional additional) {
        Additional entity = new Additional();
        entity.setId(additional.getId());
        entity.setKind(additional.getKind());
        return entity;
    }

    public static final Bottle from(final IBottle bottle) {
        Bottle entity = new Bottle();
        entity.setId(bottle.getId());
        entity.setVolume(bottle.getVolume());
        entity.setPrice(bottle.getPrice());
        entity.setBrandName(bottle.getBrandName());
        entity.setIngredient(new Beverage(bottle.getBeverageId()));
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