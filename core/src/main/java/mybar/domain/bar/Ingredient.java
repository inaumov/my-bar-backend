package mybar.domain.bar;

import mybar.api.bar.IIngredient;

import javax.persistence.*;

@Entity
@Table(name = "INGREDIENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "GROUP_NAME")
public class Ingredient implements IIngredient {

    @Id
    private int id;

    @Column(name = "KIND")
    private String kind;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

}