package mybar.domain;

import mybar.QuantityValue;
import mybar.api.IIngredient;
import mybar.api.IDrink;

import javax.persistence.*;

@Entity
public class Ingredient implements IIngredient {

    @Id
    private int id;

    @ManyToOne
    private Drink drink;

    @Column(name = "VOLUME")
    private double volume;

    @Column(name = "VALUE")
    @Enumerated(EnumType.STRING)
    private QuantityValue value;

    @ManyToOne
    @JoinColumn(name = "COCKTAIL_ID")
    private Cocktail cocktail;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public IDrink getDrink() {
        return drink;
    }

    @Override
    public double getVolume() {
        return volume;
    }

    @Override
    public QuantityValue getQuantity() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setValue(QuantityValue value) {
        this.value = value;
    }

}