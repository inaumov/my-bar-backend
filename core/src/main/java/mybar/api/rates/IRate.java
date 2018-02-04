package mybar.api.rates;

import java.util.Date;

public interface IRate {

    String getCocktailId();

    Date getRatedAt();

    Integer getStars();

}