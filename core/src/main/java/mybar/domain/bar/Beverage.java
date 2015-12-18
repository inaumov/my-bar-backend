package mybar.domain.bar;

import mybar.BeverageType;
import mybar.api.bar.IBeverage;

import javax.persistence.*;

@Entity
@DiscriminatorValue(value = "Beverage")
public class Beverage extends Ingredient implements IBeverage {

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private BeverageType beverageType;

    public Beverage() {
    }

    public Beverage(int beverageId) {
        setId(beverageId);
    }

    @Override
    public BeverageType getBeverageType() {
        return beverageType;
    }

    public void setBeverageType(BeverageType beverageType) {
        this.beverageType = beverageType;
    }

}