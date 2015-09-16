package mybar.domain;

import mybar.BeverageType;
import mybar.api.IBeverage;
import mybar.api.IIngredient;

import javax.persistence.*;

@Entity
@Table(name = "ingredient")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Ingredient implements IIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "KIND")
    private String name;

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

}