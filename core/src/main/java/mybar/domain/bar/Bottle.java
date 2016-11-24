package mybar.domain.bar;

import lombok.Getter;
import lombok.Setter;
import mybar.State;
import mybar.domain.bar.ingredient.Beverage;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "BOTTLE")
//@SequenceGenerator(name = "BOTTLE_SEQUENCE", sequenceName = "BOTTLE_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Bottle {

    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOTTLE_SEQUENCE")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "INGREDIENT_ID", nullable = false)
    public Beverage beverage;

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

}