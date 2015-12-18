package mybar.domain.bar;

import mybar.State;
import mybar.api.bar.IBottle;
import mybar.dto.bar.BottleDto;
import org.modelmapper.ModelMapper;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "BOTTLE_SEQUENCE", sequenceName = "BOTTLE_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Bottle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOTTLE_SEQUENCE")
    private int id;

    @ManyToOne
    @JoinColumn(name = "INGREDIENT_ID")
    public Ingredient ingredient;

    @Column(name = "BRAND_NAME")
    private String brandName;

    @Column(name = "VOLUME")
    private double volume;

    @Column(name = "PRICE")
    private double price;

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

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public IBottle toDto() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, BottleDto.class);
    }

}