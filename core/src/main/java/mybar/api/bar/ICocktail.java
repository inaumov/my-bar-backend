package mybar.api.bar;

import java.util.Collection;
import java.util.Map;

public interface ICocktail {

    int getId();

    String getName();

    Map<String, ? extends Collection<? extends ICocktailIngredient>> getIngredients();

    String getMenuName();

    String getDescription();

    String getImageUrl();

}