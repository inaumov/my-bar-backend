package mybar.api;

import java.util.Collection;

public interface IMenu {

    int getId();

    String getName();

    Collection<? extends IDrink> getDrinks();

}