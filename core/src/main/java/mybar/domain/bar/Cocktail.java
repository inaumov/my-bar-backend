package mybar.domain.bar;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;
import mybar.State;
import mybar.api.bar.ICocktail;
import mybar.dto.DtoFactory;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "COCKTAIL")
public class Cocktail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "NAME")
    private String name;

    /**
     * Here is the annotation to add in order to
     * Hibernate to automatically insert and update
     * CocktailToIngredientList (if any)
     */
    @OneToMany(mappedBy = "pk.cocktail", fetch = FetchType.EAGER,
            orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private List<CocktailToIngredient> cocktailToIngredientList = new LinkedList<>();

    @ManyToOne
    @JoinColumn(name = "MENU_ID")
    private Menu menu;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "AVAILABLE")
    @Enumerated(EnumType.ORDINAL)
    private State state;

    @Column(name = "IMAGE_URL", nullable = true)
    private String imageUrl;

    public void addCocktailToIngredient(CocktailToIngredient cocktailToIngredient) {
        if (!getCocktailToIngredientList().contains(cocktailToIngredient)) {
            getCocktailToIngredientList().add(cocktailToIngredient);
            cocktailToIngredient.setCocktail(this);
        }
    }

    public ICocktail toDto() {
        return DtoFactory.toDto(this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id", id)
                .add("name", name)
                .add("state", state)
                .toString();
    }

}