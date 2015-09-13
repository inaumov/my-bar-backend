package mybar.api;

public interface IStorage {

    int getId();

    <T extends IDrink> T getDrink();

    double getVolume();

    double getPrice();

}