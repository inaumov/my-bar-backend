package mybar.api;

public interface IStorage {

    int getId();

    <T extends IDrink> T getIngredient();

    double getVolume();

    double getPrice();

}