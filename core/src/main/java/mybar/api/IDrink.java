package mybar.api;

import mybar.ActiveStatus;
import mybar.Preparation;

import java.sql.Blob;
import java.util.Collection;

public interface IDrink {

    int getId();

    String getName();

    Collection<? extends IBasis> getBasisList();

    <T extends IMenu> T getMenu();

    String getDescription();

    Preparation getPreparation();

    ActiveStatus getActiveStatus();

    Blob getPicture();

    double getPrice();

}