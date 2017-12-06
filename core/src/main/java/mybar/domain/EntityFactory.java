package mybar.domain;

import mybar.api.bar.IBottle;
import mybar.api.bar.ICocktail;
import mybar.api.bar.ICocktailIngredient;
import mybar.api.bar.IMenu;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.history.IOrder;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.CocktailToIngredient;
import mybar.domain.bar.Menu;
import mybar.domain.bar.ingredient.Additive;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.domain.history.Order;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class EntityFactory {

    public static Menu from(final IMenu menu) {
        Menu entity = new Menu();
        entity.setId(menu.getId());
        entity.setName(menu.getName());
        return entity;
    }

    public static Cocktail from(final ICocktail cocktail, int menuId) {
        Cocktail entity = new Cocktail();
        entity.setId(cocktail.getId());
        entity.setName(cocktail.getName());
        entity.setDescription(cocktail.getDescription());
        entity.setImageUrl(cocktail.getImageUrl());
        Map<String, Collection<ICocktailIngredient>> allIngredients = cocktail.getIngredients();
        for (String groupName : allIngredients.keySet()) {
            Collection<ICocktailIngredient> ingredientsByGroup = allIngredients.get(groupName);
            for (ICocktailIngredient cocktailToIngredient : ingredientsByGroup) {
                entity.addCocktailToIngredient(from(groupName, cocktailToIngredient));
            }
        }
        entity.setMenuId(menuId);
        return entity;
    }

    public static CocktailToIngredient from(String groupName, final ICocktailIngredient inside) {
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
        cocktailToIngredient.setMeasurement(inside.getMeasurement());
        cocktailToIngredient.setVolume(inside.getVolume());
        return cocktailToIngredient;
    }

    public static Ingredient from(final IBeverage beverage) {
        Beverage entity = new Beverage();
        entity.setId(beverage.getId());
        entity.setKind(beverage.getKind());
        entity.setBeverageType(beverage.getBeverageType());
        return entity;
    }

    public static Ingredient from(final IDrink drink) {
        Drink entity = new Drink();
        entity.setId(drink.getId());
        entity.setKind(drink.getKind());
        entity.setDrinkType(drink.getDrinkType());
        return entity;
    }

    public static Ingredient from(final IAdditive additive) {
        Additive entity = new Additive();
        entity.setId(additive.getId());
        entity.setKind(additive.getKind());
        return entity;
    }

    public static Bottle from(final IBottle bottle) {
        Bottle entity = new Bottle();
        entity.setId(bottle.getId());
        entity.setVolume(bottle.getVolume());
        entity.setPrice(bottle.getPrice());
        entity.setBrandName(bottle.getBrandName());
        entity.setBeverage(new Beverage(bottle.getBeverage().getId()));
        entity.setInShelf(bottle.isInShelf());
        entity.setImageUrl(bottle.getImageUrl());
        return entity;
    }

    public static Order from(final IOrder order) {
        Order entity = new Order();
        entity.setId(order.getId());
        entity.setAmount(order.getAmount());
        entity.setSold(order.getSold());
        entity.setOrderStatus(order.getOrderStatus());
        return entity;
    }

}