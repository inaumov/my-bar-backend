package mybar.domain.bar;

import com.google.common.base.MoreObjects;
import mybar.dto.bar.MenuDto;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Menu {

    @Id
    private int id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
    private Collection<Cocktail> cocktails;

    public Menu() {
        cocktails = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Cocktail> getCocktails() {
        return cocktails;
    }

    public void addCocktail(Cocktail cocktail) {
        if (!getCocktails().contains(cocktail)) {
            getCocktails().add(cocktail);
            cocktail.setMenu(this);
        }
    }

    public MenuDto toDto() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, MenuDto.class);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id", id)
                .add("name", name)
                .toString();

    }

}