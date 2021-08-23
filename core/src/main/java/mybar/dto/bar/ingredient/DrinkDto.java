package mybar.dto.bar.ingredient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mybar.api.bar.ingredient.DrinkType;
import mybar.api.bar.ingredient.IDrink;

@Getter
@Setter
@ToString(callSuper = true)
public class DrinkDto extends IngredientBaseDto implements IDrink {

    private DrinkType drinkType;
}