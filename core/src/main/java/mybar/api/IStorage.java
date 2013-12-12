package mybar.api;

public interface IStorage {

    int getId();

    <T extends IIngredient>T getIngredient();

    double getVolume();

    double getPrice();

}