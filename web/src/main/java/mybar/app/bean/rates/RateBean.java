package mybar.app.bean.rates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mybar.api.rates.IRate;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RateBean implements IRate {

    private String cocktailId;
    private Date ratedAt;
    private Integer stars;

    public RateBean(IRate rate) {
        this.cocktailId = rate.getCocktailId();
        this.ratedAt = rate.getRatedAt();
        this.stars = rate.getStars();
    }
}