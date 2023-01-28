package mybar.api.bar.ingredient;

public interface IDrink extends IIngredient {

    String GROUP_NAME = "drinks";

    DrinkType getDrinkType();

    default String getGroupName() {
        return IDrink.GROUP_NAME;
    }

}