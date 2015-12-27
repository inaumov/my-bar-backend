package mybar.domain.bar.ingredient;

import mybar.api.bar.ingredient.IIngredient;

import javax.persistence.*;

@Entity
@Table(name = "INGREDIENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "GROUP_NAME")
@AttributeOverride(name = "groupName",
        column = @Column(name = "GROUP_NAME", nullable = false, length = 8,
                insertable = false, updatable = false))
public class Ingredient implements IIngredient {

    @Id
    private int id;

    @Column(name = "KIND")
    private String kind;

    private String groupName;

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

    public String getGroupName() {
        return groupName;
    }

}