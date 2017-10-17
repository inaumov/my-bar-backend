package mybar.dto.bar.ingredient;

import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ingredient.DrinkType;
import mybar.api.bar.ingredient.IDrink;

@Getter
@Setter
public class DrinkDto extends IngredientBaseDto implements IDrink {

    private DrinkType drinkType;
}