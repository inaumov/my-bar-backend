package mybar.api;

import mybar.ActiveStatus;

import java.sql.Blob;
import java.util.Collection;

public interface ICocktail {

    int getId();

    String getName();

    Collection<? extends IIngredient> getIngredients();

    <T extends IMenu> T getMenu();

    String getDescription();

    ActiveStatus getActiveStatus();

    Blob getPicture();

    double getPrice();

}