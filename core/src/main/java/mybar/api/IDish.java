package mybar.api;

import mybar.ActiveStatus;
import mybar.DishType;

import java.sql.Blob;
import java.util.Collection;

public interface IDish {

    int getId();

    String getName();

    Collection<? extends IBasis> getBasisList();

    <T extends ICategory> T getCategory();

    String getDescription();

    DishType getDishType();

    ActiveStatus getActiveStatus();

    Blob getPicture();

    double getPrice();

}