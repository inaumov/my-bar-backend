package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import mybar.BeverageType;
import mybar.api.bar.ingredient.IBeverage;
import mybar.app.bean.bar.View;

import java.util.Objects;

public class BeverageBean implements IBeverage {

    @JsonView({View.CocktailWithDetails.class, View.Shelf.class})
    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private String kind;

    private BeverageType beverageType;

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

    @Override
    public BeverageType getBeverageType() {
        return beverageType;
    }

    public void setBeverageType(BeverageType beverageType) {
        this.beverageType = beverageType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id", id)
                .add("kind", kind)
                .add("beverageType", beverageType)
                .toString();
    }
}