package mybar.api.bar.ingredient;

public interface IBeverage extends IIngredient {

    public static final String GROUP_NAME = "beverages";

    BeverageType getBeverageType();

}