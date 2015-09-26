package mybar.api.bar;

import java.util.Collection;

public interface IMenu {

    int getId();

    String getName();

    Collection<? extends ICocktail> getCocktails();

}