package mybar.domain.bar;

import lombok.Getter;
import lombok.Setter;
import mybar.domain.bar.ingredient.Beverage;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "BOTTLE")
@GenericGenerator(name = "bottle_id", strategy = "mybar.domain.EntityIdGenerator")
public class Bottle {

    @Id
    @GeneratedValue(generator = "bottle_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "INGREDIENT_ID", nullable = false)
    public Beverage beverage;

    @Column(name = "BRAND_NAME")
    private String brandName;

    @Column(name = "VOLUME")
    private double volume;

    @Column(name = "PRICE", scale = 2)
    private BigDecimal price;

    @Column(name = "IN_SHELF")
    private boolean inShelf;

    @Column(name = "IMAGE_URL", nullable = true)
    private String imageUrl;

}