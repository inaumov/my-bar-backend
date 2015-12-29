package mybar.domain.bar;

import mybar.api.bar.IMenu;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Menu implements IMenu {

    @Id
    private int id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
    private Collection<Cocktail> cocktails;

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

    public Collection<Cocktail> getCocktails() {
        return cocktails;
    }

    public void setCocktails(Collection<Cocktail> cocktails) {
        this.cocktails = cocktails;
    }

}