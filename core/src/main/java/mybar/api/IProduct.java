package mybar.api;

public interface IProduct {

    int getId();

    <T extends IIngredient> T getIngredient();

    String getBrandName();

    double getVolume();

    double getPrice();

}