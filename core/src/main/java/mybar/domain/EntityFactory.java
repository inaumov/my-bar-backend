package mybar.domain;

import mybar.api.*;

public class EntityFactory {

    public static final Menu from(final IMenu menu) {
        Menu entity = new Menu();
        entity.setId(menu.getId());
        entity.setName(menu.getName());
        return entity;
    }

    public static final Drink from(final IDrink ingredient) {
        Drink entity = new Drink();
        entity.setId(ingredient.getId());
        entity.setName(ingredient.getName());
        return entity;
    }

    public static final Cocktail from (final ICocktail cocktail) {
        Cocktail entity = new Cocktail();
        entity.setId(cocktail.getId());
        entity.setName(cocktail.getName());
        entity.setDescription(cocktail.getDescription());
        entity.setActiveStatus(cocktail.getActiveStatus());
        entity.setPicture(cocktail.getPicture());
        entity.setMenu(from(cocktail.getMenu()));
        return entity;
    }

    public static final Ingredient from(final IIngredient ingredient) {
        Ingredient entity = new Ingredient();
        entity.setId(ingredient.getId());
        entity.setValue(ingredient.getQuantity());
        entity.setVolume(ingredient.getVolume());
        entity.setDrink(from(ingredient.getDrink()));
        return entity;
    }

    public static final Storage from(final IStorage storage) {
        Storage entity = new Storage();
        entity.setId(storage.getId());
        entity.setVolume(storage.getVolume());
        entity.setPrice(storage.getPrice());
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