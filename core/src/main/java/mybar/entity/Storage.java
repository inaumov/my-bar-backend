package mybar.entity;

import mybar.api.IStorage;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "STORAGE_SEQUENCE", sequenceName = "STORAGE_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Storage implements IStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STORAGE_SEQUENCE")
    private int id;

    @ManyToOne
    @JoinColumn(name = "INGREDIENT_ID")
    public Ingredient ingredient;

    @Column(name = "VOLUME")
    private double volume;

    @Column(name = "PRICE")
    private double price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient basis) {
        this.ingredient = basis;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}