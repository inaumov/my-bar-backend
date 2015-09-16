package mybar.domain;

import mybar.DrinkType;
import mybar.api.IDrink;

import javax.persistence.*;

@Entity
@Table(name = "ingredient")
public class Drink extends Ingredient implements IDrink {

    @Column(name = "INGREDIENT_TYPE")
    @Enumerated(EnumType.STRING)
    private DrinkType drinkType;

    @Override
    public DrinkType getDrinkType() {
        return drinkType;
    }

    public void setDrinkType(DrinkType drinkType) {
        this.drinkType = drinkType;
    }

}