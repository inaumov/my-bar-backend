package mybar.domain.bar.ingredient;

import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ingredient.IIngredient;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "INGREDIENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "GROUP_NAME")
@NamedQueries({
        @NamedQuery(
                name = "findAll",
                query = "SELECT i FROM Ingredient i order by i.class, i.kind"
        ),
        @NamedQuery(
                name = "findByGroupName",
                query = "SELECT i FROM Ingredient i WHERE TYPE(i) = :type order by i.kind"
        )
})
public class Ingredient implements IIngredient {

    @Id
    private int id;

    @Column(name = "KIND")
    private String kind;

}