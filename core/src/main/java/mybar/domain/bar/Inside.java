package mybar.domain.bar;

import mybar.UnitsValue;
import mybar.api.bar.IInside;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.dto.bar.InsideDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.persistence.*;

@Entity
public class Inside {

    @Id
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Ingredient ingredient;

    @Column(name = "VOLUME")
    private double volume;

    @Column(name = "UNITS")
    @Enumerated(EnumType.STRING)
    private UnitsValue unitsValue;

    @ManyToOne
    @JoinColumn(name = "COCKTAIL_ID")
    private Cocktail cocktail;

    public int getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getVolume() {
        return volume;
    }

    public UnitsValue getUnitsValue() {
        return unitsValue;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setUnitsValue(UnitsValue unitsValue) {
        this.unitsValue = unitsValue;
    }

    public InsideDto toDto() {
        PropertyMap<Inside, InsideDto> insideMap = new PropertyMap<Inside, InsideDto>() {
            @Override
            protected void configure() {
                map().setIngredientId(source.getIngredient().getId());
                skip().setMissing(false);
            }
        };
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(insideMap);

        return modelMapper.map(this, InsideDto.class);
    }

}