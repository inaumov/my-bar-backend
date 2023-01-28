package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ingredient.DrinkType;
import mybar.api.bar.ingredient.IDrink;
import mybar.app.bean.bar.View;

@Getter
@Setter
public class DrinkBean implements IDrink {

    @JsonView(View.CocktailWithDetails.class)
    private Integer id;

    @JsonView(View.CocktailWithDetails.class)
    private String kind;

    private DrinkType drinkType;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id", id)
                .add("kind", kind)
                .add("drinkType", drinkType)
                .toString();
    }

}