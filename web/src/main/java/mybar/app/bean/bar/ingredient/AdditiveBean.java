package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ingredient.IAdditive;
import mybar.app.bean.bar.View;

@Getter
@Setter
public class AdditiveBean implements IAdditive {

    @JsonView(View.CocktailWithDetails.class)
    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private String kind;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id", id)
                .add("kind", kind)
                .toString();
    }

}