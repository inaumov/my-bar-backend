package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ingredient.IAdditive;
import mybar.app.bean.bar.View;

@Getter
@Setter
public class AdditiveBean implements IAdditive {

    @JsonView(View.CocktailWithDetails.class)
    private Integer id;

    @JsonView(View.CocktailWithDetails.class)
    private String kind;

}