package mybar.api.bar.ingredient;

public interface IIngredient {

    Integer getId();

    String getKind();

    default String getGroupName() {
        return "";
    }

}