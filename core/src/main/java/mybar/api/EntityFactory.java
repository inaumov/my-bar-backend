package mybar.api;

import mybar.entity.*;


// FIXME: casting is required to avoid compilation error "ambiguous method declaration"
public class EntityFactory {

    public static final Menu from(final IMenu menu) {
        Menu entity = new Menu();
        entity.setId(menu.getId());
        entity.setName(menu.getName());
        return entity;
    }

    public static final Ingredient from(final IIngredient ingredient) {
        Ingredient entity = new Ingredient();
        entity.setId(ingredient.getId());
        entity.setName(ingredient.getName());
        return entity;
    }

    public static final Drink from (final IDrink drink) {
        Drink entity = new Drink();
        entity.setId(drink.getId());
        entity.setName(drink.getName());
        entity.setDescription(drink.getDescription());
        entity.setPreparation(drink.getPreparation());
        entity.setActiveStatus(drink.getActiveStatus());
        entity.setPicture(drink.getPicture());
        entity.setMenu(from((Menu) drink.getMenu()));
        return entity;
    }

    public static final Basis from(final IBasis basis) {
        Basis entity = new Basis();
        entity.setId(basis.getId());
        entity.setValue(basis.getValue());
        entity.setVolume(basis.getVolume());
        entity.setIngredient(from((Ingredient)basis.getIngredient()));
        return entity;
    }

    public static final Storage from(final IStorage storage) {
        Storage entity = new Storage();
        entity.setId(storage.getId());
        entity.setVolume(storage.getVolume());
        entity.setPrice(storage.getPrice());
        entity.setIngredient(from((Ingredient)storage.getIngredient()));
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