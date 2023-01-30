package mybar.domain.bar;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.IMenu;
import mybar.dto.DtoFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="menu_id", referencedColumnName="id")
    private Collection<Cocktail> cocktails = new ArrayList<>();

    public void addCocktail(Cocktail cocktail) {
        if (!getCocktails().contains(cocktail)) {
            getCocktails().add(cocktail);
            cocktail.setMenuId(id);
        }
    }

    public IMenu toDto() {
        return DtoFactory.toDto(this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id", id)
                .add("name", name)
                .toString();

    }

}