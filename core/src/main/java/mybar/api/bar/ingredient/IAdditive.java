package mybar.api.bar.ingredient;

public interface IAdditive extends IIngredient {

    String GROUP_NAME = "additives";

    default String getGroupName() {
        return IAdditive.GROUP_NAME;
    }

}