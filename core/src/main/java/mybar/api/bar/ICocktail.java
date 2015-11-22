package mybar.api.bar;

import mybar.State;

import java.sql.Blob;
import java.util.Collection;

public interface ICocktail {

    int getId();

    String getName();

    Collection<? extends IInside> getIngredients();

    int getMenuId();

    String getDescription();

    State getState();

    String getImageUrl();

}