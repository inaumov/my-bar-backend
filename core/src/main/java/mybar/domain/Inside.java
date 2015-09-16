package mybar.domain;

import mybar.QuantityValue;
import mybar.api.IIngredient;
import mybar.api.IInside;

import javax.persistence.*;

@Entity
public class Inside implements IInside {

    @Id
    private int id;

    @ManyToOne
    private Ingredient ingredient;

    @Column(name = "VOLUME")
    private double volume;

    @Column(name = "QUANTITY_VALUE")
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
    public IIngredient getIngredient() {
        return ingredient;
    }

    @Override
    public double getVolume() {
        return volume;
    }

    @Override
    public QuantityValue getQuantityValue() {
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