package mybar.dto;

import lombok.Getter;
import lombok.Setter;
import mybar.api.rates.IRate;

import java.util.Date;

@Getter
@Setter
public class RateDto implements IRate {
    private String cocktailId;
    private Date ratedAt;
    private Integer stars;

    public static RateDto ofStars(int stars) {
        RateDto rateDto = new RateDto();
        rateDto.setStars(stars);
        return rateDto;
    }

}
