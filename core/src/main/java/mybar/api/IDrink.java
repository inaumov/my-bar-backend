package mybar.api;

import mybar.BeverageType;

public interface IDrink {

    int getId();

    String getKind();

    BeverageType getBeverageType();
}