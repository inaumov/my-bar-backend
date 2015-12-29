package mybar.domain.bar;

import mybar.State;
import mybar.api.bar.ICocktail;
import mybar.dto.bar.CocktailDto;
import mybar.util.ModelMapperConverters;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.persistence.*;
import java.util.Collection;

@Entity
@SequenceGenerator(name = "COCKTAIL_SEQUENCE", sequenceName = "COCKTAIL_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Cocktail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COCKTAIL_SEQUENCE")
    private int id;

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "MENU_ID")
    private Menu menu;

    @OneToMany(mappedBy = "cocktail", fetch = FetchType.EAGER) // TODO: fetch lazily
    private Collection<Inside> insideList;

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

    public Collection<Inside> getInsideItems() {
        return insideList;
    }

    public void setInsideItems(Collection<Inside> insideList) {
        this.insideList = insideList;
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
                using(ModelMapperConverters.INSIDES_CONVERTER).map(source.getInsideItems()).setInsideItems(null);
                map().setMenuId(source.getMenu().getId());
            }
        };
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(insidesMap);

        return modelMapper.map(this, CocktailDto.class);
    }

}