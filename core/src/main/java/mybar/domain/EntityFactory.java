package mybar.domain;

import mybar.api.*;

public class EntityFactory {

    public static final Menu from(final IMenu menu) {
        Menu entity = new Menu();
        entity.setId(menu.getId());
        entity.setName(menu.getName());
        return entity;
    }

    public static final Drink from(final IDrink drink) {
        Drink entity = new Drink();
        entity.setId(drink.getId());
        entity.setKind(drink.getKind());
        entity.setBeverageType(drink.getBeverageType());
        return entity;
    }

    public static final Cocktail from (final ICocktail cocktail) {
        Cocktail entity = new Cocktail();
        entity.setId(cocktail.getId());
        entity.setName(cocktail.getName());
        entity.setDescription(cocktail.getDescription());
        entity.setState(cocktail.getState());
        entity.setPicture(cocktail.getPicture());
        entity.setMenu(from(cocktail.getMenu()));
        return entity;
    }

    public static final Inside from(final IInside ingredient) {
        Inside entity = new Inside();
        entity.setId(ingredient.getId());
        entity.setValue(ingredient.getQuantityValue());
        entity.setVolume(ingredient.getVolume());
        entity.setDrink(from(ingredient.getDrink()));
        return entity;
    }

    public static final Product from(final IProduct storage) {
        Product entity = new Product();
        entity.setId(storage.getId());
        entity.setVolume(storage.getVolume());
        entity.setPrice(storage.getPrice());
        entity.setBrandName(storage.getBrandName());
        entity.setDrink(from(storage.getDrink()));
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