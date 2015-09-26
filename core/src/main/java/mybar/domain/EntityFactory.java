package mybar.domain;

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
        entity.setPicture(cocktail.getPicture());
        entity.setMenu(from(cocktail.getMenu()));
        return entity;
    }

    public static final Inside from(final IInside inside) {
        Inside entity = new Inside();
        entity.setId(inside.getId());
        entity.setValue(inside.getQuantityValue());
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

    public static final Product from(final IProduct product) {
        Product entity = new Product();
        entity.setId(product.getId());
        entity.setVolume(product.getVolume());
        entity.setPrice(product.getPrice());
        entity.setBrandName(product.getBrandName());
        IIngredient ingredient = product.getIngredient();
        if (ingredient instanceof IBeverage) {
            entity.setIngredient(from((IBeverage) ingredient));
        } else if (ingredient instanceof IDrink) {
            entity.setIngredient(from((IDrink) ingredient));
        } else if (ingredient instanceof IAdditional) {
            entity.setIngredient(from((IAdditional) ingredient));
        }
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