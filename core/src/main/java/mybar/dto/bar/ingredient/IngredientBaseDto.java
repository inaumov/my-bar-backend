package mybar.dto.bar.ingredient;

import mybar.api.bar.ingredient.IIngredient;

public class IngredientBaseDto implements IIngredient {

    private int id;
    private String kind;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

}