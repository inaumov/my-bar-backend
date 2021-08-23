package mybar.dto.bar.ingredient;

import lombok.ToString;
import mybar.api.bar.ingredient.IAdditive;

@ToString(callSuper = true)
public class AdditiveDto extends IngredientBaseDto implements IAdditive {

}