package mybar.api.bar;

import java.util.Collection;
import java.util.Map;

public interface ICocktail {

    int getId();

    String getName();

    <T extends ICocktailIngredient> Map<String, Collection<T>> getIngredients();

    String getMenuName();

    String getDescription();

    String getImageUrl();

}