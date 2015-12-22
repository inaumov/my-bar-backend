package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.bar.ingredient.IAdditive;
import mybar.app.bean.bar.View;

public class AdditiveBean implements IAdditive {

    @JsonView(View.CocktailWithDetails.class)
    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private String name;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getKind() {
        return name;
    }

    public void setKind(String name) {
        this.name = name;
    }

    public static AdditiveBean from(IAdditive ingredient) {
        AdditiveBean bean = new AdditiveBean();
        bean.setId(ingredient.getId());
        bean.setKind(ingredient.getKind());
        return bean;
    }

}