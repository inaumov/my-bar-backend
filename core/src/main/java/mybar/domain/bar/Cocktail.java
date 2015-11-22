package mybar.domain.bar;

import mybar.State;
import mybar.api.bar.ICocktail;

import javax.persistence.*;
import java.util.Collection;

@Entity
@SequenceGenerator(name = "COCKTAIL_SEQUENCE", sequenceName = "COCKTAIL_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Cocktail implements ICocktail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COCKTAIL_SEQUENCE")
    private int id;

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "MENU_ID")
    private Menu menu;

    @OneToMany(mappedBy = "cocktail", fetch = FetchType.EAGER) // TODO: fetch lazily
    private Collection<Inside> ingredients;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "AVAILABLE")
    @Enumerated(EnumType.ORDINAL)
    private State state;

    @Column(name = "IMAGE_URL", nullable = true)
    private String imageUrl;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Inside> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Collection<Inside> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int getMenuId() {
        return menu.getId();
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}