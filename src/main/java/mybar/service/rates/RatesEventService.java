package mybar.service.rates;

import lombok.extern.slf4j.Slf4j;
import mybar.api.rates.IRate;
import mybar.dto.RateDto;
import mybar.events.common.api.IEventProducer;
import mybar.utils.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
public class RatesEventService {

    private static final Range<Integer> starsRange = Range.of(Range.Bound.inclusive(1), Range.Bound.inclusive(10));

    @Value(value = "${my-bar.events.rates-topic-name}")
    private String ratesTopicName;

    private final IEventProducer<RateDto> ratesEventProducer;
    private final RatesService ratesService;

    @Autowired
    public RatesEventService(IEventProducer<RateDto> ratesEventProducer, RatesService ratesService) {
        this.ratesEventProducer = ratesEventProducer;
        this.ratesService = ratesService;
    }

    public IRate rateCocktail(String username, String cocktailId, Integer stars) {
        Preconditions.checkArgument(StringUtils.hasText(username), "Username is required.");
        Preconditions.checkArgument(StringUtils.hasText(cocktailId), "Cocktail id is required.");
        Preconditions.checkArgument(stars != null && starsRange.contains(stars), "Stars number should be from 1 to 10.");
        ratesService.checkCocktailExists(cocktailId);

        RateDto rateDto = RateDto.ofStars(stars);
        Instant send = ratesEventProducer.send(ratesTopicName, username, cocktailId, rateDto);
        rateDto.setCocktailId(cocktailId);
        rateDto.setRatedAt(LocalDateTime.ofInstant(send, ZoneId.systemDefault()));
        return rateDto;
    }

}