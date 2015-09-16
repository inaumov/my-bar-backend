package mybar.domain;

import mybar.BeverageType;
import mybar.api.IDrink;

import javax.persistence.*;

@Entity
public class Drink implements IDrink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "KIND")
    private String name;

    @Column(name = "BEVERAGE_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private BeverageType beverageType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getKind() {
        return name;
    }

    public void setKind(String name) {
        this.name = name;
    }

    @Override
    public BeverageType getBeverageType() {
        return beverageType;
    }

    public void setBeverageType(BeverageType beverageType) {
        this.beverageType = beverageType;
    }

}