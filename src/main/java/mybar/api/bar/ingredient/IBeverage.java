package mybar.api.bar.ingredient;

public interface IBeverage extends IIngredient {

    String GROUP_NAME = "beverages";

    BeverageType getBeverageType();

    default String getGroupName() {
        return IBeverage.GROUP_NAME;
    }

}