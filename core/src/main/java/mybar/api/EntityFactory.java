package mybar.api;

import mybar.entity.Basis;
import mybar.entity.Category;
import mybar.entity.Dish;
import mybar.entity.Ingredient;
import mybar.entity.Order;
import mybar.entity.Storage;


// FIXME: casting is required to avoid compilation error "ambiguous method declaration"
public class EntityFactory {

    public static final Category from(final ICategory category) {
        Category entity = new Category();
        entity.setId(category.getId());
        entity.setName(category.getName());
        return entity;
    }

    public static final Ingredient from(final IIngredient ingredient) {
        Ingredient entity = new Ingredient();
        entity.setId(ingredient.getId());
        entity.setName(ingredient.getName());
        return entity;
    }

    public static final Dish from (final IDish dish) {
        Dish entity = new Dish();
        entity.setId(dish.getId());
        entity.setName(dish.getName());
        entity.setDescription(dish.getDescription());
        entity.setDishType(dish.getDishType());
        entity.setActiveStatus(dish.getActiveStatus());
        entity.setPicture(dish.getPicture());
        entity.setCategory(from((Category)dish.getCategory()));
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