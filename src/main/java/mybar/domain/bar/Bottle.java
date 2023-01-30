package mybar.domain.bar;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;
import mybar.domain.bar.ingredient.Beverage;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "bottle")
@GenericGenerator(name = "bottle_id", strategy = "mybar.domain.EntityIdGenerator")
public class Bottle {

    @Id
    @GeneratedValue(generator = "bottle_id")
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    public Beverage beverage;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "volume")
    private double volume;

    @Column(name = "price", scale = 2)
    private BigDecimal price;

    @Column(name = "in_shelf", columnDefinition = "boolean default false")
    private boolean inShelf;

    @Column(name = "image_url")
    private String imageUrl;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id", id)
                .add("brandName", brandName)
                .toString();
    }

}