package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ingredient.BeverageType;
import mybar.api.bar.ingredient.IBeverage;
import mybar.app.bean.bar.View;

@Getter
@Setter
public class BeverageBean implements IBeverage {

    @JsonView({View.CocktailWithDetails.class, View.Shelf.class})
    private Integer id;

    @JsonView(View.CocktailWithDetails.class)
    private String kind;

    private BeverageType beverageType;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id", id)
                .add("kind", kind)
                .add("beverageType", beverageType)
                .toString();
    }
}