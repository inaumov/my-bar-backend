package mybar.domain;

import mybar.QuantityValue;
import mybar.api.IBasis;
import mybar.api.IIngredient;

import javax.persistence.*;

@Entity
public class Basis implements IBasis {

    @Id
    private int id;

    @ManyToOne
    private Ingredient ingredient;

    @Column(name = "VOLUME")
    private double volume;

    @Column(name = "VALUE")
    @Enumerated(EnumType.STRING)
    private QuantityValue value;

    @ManyToOne
    @JoinColumn(name = "DRINK_ID")
    private Drink drink;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public IIngredient getIngredient() {
        return ingredient;
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

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setValue(QuantityValue value) {
        this.value = value;
    }

}