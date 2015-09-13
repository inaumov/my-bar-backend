package mybar.domain;

import mybar.State;
import mybar.api.ICocktail;

import javax.persistence.*;
import java.sql.Blob;
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
    private Collection<Ingredient> ingredients;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Transient
    private double price;

    @Column(name = "AVAILABLE")
    @Enumerated(EnumType.ORDINAL)
    private State state;

    @Column(name = "COVER", nullable = true)
    private String cover;

    @Lob
    @Column(name = "IMAGE", nullable = true)
    private Blob picture;

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

    public Collection<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Collection<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public Blob getPicture() {
        return picture;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }

}