package mybar.domain;

import mybar.api.IProduct;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "STORAGE_SEQUENCE", sequenceName = "STORAGE_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Product implements IProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STORAGE_SEQUENCE")
    private int id;

    @ManyToOne
    @JoinColumn(name = "DRINK_ID")
    public Drink drink;

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

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
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