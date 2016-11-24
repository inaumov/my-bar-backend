package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.bar.ingredient.IAdditive;
import mybar.app.bean.bar.View;

public class AdditiveBean implements IAdditive {

    @JsonView(View.CocktailWithDetails.class)
    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private String kind;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

}