package mybar.dto.bar.ingredient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mybar.api.bar.ingredient.IIngredient;

@Getter
@Setter
@ToString
public class IngredientBaseDto implements IIngredient {

    private Integer id;
    private String kind;
}