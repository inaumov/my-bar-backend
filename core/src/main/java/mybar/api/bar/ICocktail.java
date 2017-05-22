package mybar.api.bar;

import java.util.Collection;
import java.util.Map;

public interface ICocktail extends IModifiable {

    String getName();

    <T extends ICocktailIngredient> Map<String, Collection<T>> getIngredients();

    String getMenuName();

    String getDescription();

    String getImageUrl();

}