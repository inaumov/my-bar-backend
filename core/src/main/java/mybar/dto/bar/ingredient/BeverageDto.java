package mybar.dto.bar.ingredient;

import mybar.BeverageType;
import mybar.api.bar.ingredient.IBeverage;

public class BeverageDto extends IngredientBaseDto implements IBeverage {

    private BeverageType beverageType;

    @Override
    public BeverageType getBeverageType() {
        return beverageType;
    }

    public void setBeverageType(BeverageType beverageType) {
        this.beverageType = beverageType;
    }

}