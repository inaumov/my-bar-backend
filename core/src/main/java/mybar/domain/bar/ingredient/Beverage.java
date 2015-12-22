package mybar.domain.bar.ingredient;

import mybar.BeverageType;
import mybar.api.bar.ingredient.IBeverage;
import mybar.domain.bar.Bottle;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(value = "Beverage")
public class Beverage extends Ingredient implements IBeverage {

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private BeverageType beverageType;

    @OneToMany(mappedBy = "beverage", fetch = FetchType.EAGER)
    private List<Bottle> bottles;

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

    public List<Bottle> getBottles() {
        return bottles;
    }

    public void setBottles(List<Bottle> bottles) {
        this.bottles = bottles;
    }

}