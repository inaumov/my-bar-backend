package mybar.domain.bar;

import mybar.api.bar.IIngredient;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "ingredient")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ENTITY")
public class Ingredient implements IIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "KIND")
    private String name;

/*    @OneToMany(mappedBy = "ingredient", fetch = FetchType.EAGER)
    private List<Product> products;*/

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