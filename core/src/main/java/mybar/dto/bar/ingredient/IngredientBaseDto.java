package mybar.dto.bar.ingredient;

import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ingredient.IIngredient;

@Getter
@Setter
public class IngredientBaseDto implements IIngredient {

    private Integer id;
    private String kind;
}