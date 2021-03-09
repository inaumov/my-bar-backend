package mybar.api.rates;

import java.time.LocalDateTime;

public interface IRate {

    String getCocktailId();

    LocalDateTime getRatedAt();

    Integer getStars();

}