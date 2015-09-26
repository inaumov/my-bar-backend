package mybar.domain.bar;

import mybar.api.bar.IProduct;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "STORAGE_SEQUENCE", sequenceName = "STORAGE_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Product implements IProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STORAGE_SEQUENCE")
    private int id;

    @ManyToOne
    @JoinColumn(name = "INGREDIENT_ID")
    public Ingredient ingredient;

    @Column(name = "BRAND_NAME")
    private String brandName;

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

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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