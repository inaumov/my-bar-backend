package mybar.domain.bar;

import com.google.common.base.MoreObjects;
import mybar.State;
import mybar.dto.bar.CocktailDto;
import mybar.util.ModelMapperConverters;
import org.hibernate.annotations.Cascade;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

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
    @OneToMany(mappedBy = "pk.cocktail", fetch = FetchType.LAZY,
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

    public List<CocktailToIngredient> getCocktailToIngredientList() {
        return cocktailToIngredientList;
    }

    public void addCocktailToIngredient(CocktailToIngredient cocktailToIngredient) {
        if (!getCocktailToIngredientList().contains(cocktailToIngredient)) {
            getCocktailToIngredientList().add(cocktailToIngredient);
            cocktailToIngredient.setCocktail(this);
        }
    }

    public void setCocktailToIngredientList(List<CocktailToIngredient> cocktailToIngredientList) {
        this.cocktailToIngredientList = cocktailToIngredientList;
    }

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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CocktailDto toDto() {

        PropertyMap<Cocktail, CocktailDto> insidesMap = new PropertyMap<Cocktail, CocktailDto>() {
            @Override
            protected void configure() {
                using(ModelMapperConverters.INSIDES_CONVERTER).map(source.getCocktailToIngredientList()).setInsideItems(null);
                map().setMenuId(source.getMenu().getId());
            }
        };
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(insidesMap);

        return modelMapper.map(this, CocktailDto.class);
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