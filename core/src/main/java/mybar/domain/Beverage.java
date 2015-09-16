package mybar.domain;

import mybar.BeverageType;
import mybar.api.IBeverage;

import javax.persistence.*;

@Entity
@Table(name = "ingredient")
public class Beverage extends Ingredient implements IBeverage {

    @Column(name = "INGREDIENT_TYPE")
    @Enumerated(EnumType.STRING)
    private BeverageType beverageType;

    @Override
    public BeverageType getBeverageType() {
        return beverageType;
    }

    public void setBeverageType(BeverageType beverageType) {
        this.beverageType = beverageType;
    }

}