package mybar.service.rates;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import mybar.api.rates.IRate;
import mybar.dto.RateDto;
import common.events.api.IEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
public class RatesEventService {

    private static final Range<Integer> starsRange = Range.of(Range.Bound.inclusive(1), Range.Bound.inclusive(10));

    @Value(value = "${kafka.events.rates-topic-name}")
    private String ratesTopicName;

    private final IEventProducer<RateDto> eventProducer;
    private final RatesService ratesService;

    @Autowired
    public RatesEventService(IEventProducer<RateDto> eventProducer, RatesService ratesService) {
        this.eventProducer = eventProducer;
        this.ratesService = ratesService;
    }

    public IRate rateCocktail(String username, String cocktailId, Integer stars) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(username), "Username is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cocktailId), "Cocktail id is required.");
        Preconditions.checkArgument(stars != null && starsRange.contains(stars), "Stars number should be from 1 to 10.");
        ratesService.checkCocktailExists(cocktailId);

        RateDto rateDto = RateDto.ofStars(stars);
        Instant send = eventProducer.send(ratesTopicName, username, cocktailId, rateDto);
        rateDto.setCocktailId(cocktailId);
        rateDto.setRatedAt(LocalDateTime.ofInstant(send, ZoneId.systemDefault()));
        return rateDto;
    }

}