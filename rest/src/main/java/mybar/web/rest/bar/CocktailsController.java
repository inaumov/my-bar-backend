package mybar.web.rest.bar;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Strings;
import common.providers.availability.IAvailabilityCalculator;
import mybar.api.bar.ICocktail;
import mybar.app.RestBeanConverter;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.View;
import mybar.service.bar.CocktailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cocktails")
public class CocktailsController {

    private Logger logger = LoggerFactory.getLogger(CocktailsController.class);

    private final CocktailsService cocktailsService;
    private final IAvailabilityCalculator<CocktailBean> availabilityCalculator;

    @Autowired
    public CocktailsController(CocktailsService cocktailsService, @Qualifier("CocktailAvailabilityCalculator") IAvailabilityCalculator<CocktailBean> availabilityCalculator) {
        this.cocktailsService = cocktailsService;
        this.availabilityCalculator = availabilityCalculator;
    }

    //-------------------Retrieve All Cocktails--------------------------------------------------------

    @Secured("ROLE_USER")
    @JsonView(View.Cocktail.class)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<CocktailBean>>> allCocktails(@RequestParam(value = "filter", required = false) String menuNameParam) {
        if (!Strings.isNullOrEmpty(menuNameParam)) {
            Map<String, List<CocktailBean>> cocktailsForMenu = findCocktailsForMenu(menuNameParam);
            return new ResponseEntity<>(cocktailsForMenu, HttpStatus.OK);
        }
        Map<String, List<ICocktail>> cocktailsMap = cocktailsService.getAllCocktails();
        if (cocktailsMap.isEmpty()) {
            logger.info("Cocktail list is empty.");
            return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.OK);
        }
        Map<String, List<CocktailBean>> responseMap = convertAndCalculateAvailability(cocktailsMap);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    private Map<String, List<CocktailBean>> findCocktailsForMenu(String menuName) {
        logger.info("Fetching cocktails for menu [{}]...", menuName);
        List<ICocktail> cocktailsList = cocktailsService.getAllCocktailsForMenu(menuName);
        if (cocktailsList.isEmpty()) {
            logger.info(MessageFormat.format("Cocktails list for menu [{0}] is empty.", menuName));
            return Collections.emptyMap();
        }

        List<CocktailBean> converted = convertAndCalculateAvailability(cocktailsList);
        logger.info(MessageFormat.format("Found {0} cocktails for menu [{1}]", converted.size(), menuName));
        return Collections.singletonMap(menuName, converted);
    }

    private Map<String, List<CocktailBean>> convertAndCalculateAvailability(Map<String, List<ICocktail>> cocktailsMap) {
        Map<String, List<CocktailBean>> newConvertedMap = new HashMap<>();
        for (String menuName : cocktailsMap.keySet()) {
            List<ICocktail> cocktails = cocktailsMap.get(menuName);
            List<CocktailBean> converted = convertAndCalculateAvailability(cocktails);
            newConvertedMap.put(menuName, converted);
            logger.info(MessageFormat.format("Found {0} cocktails for menu [{1}]", cocktails.size(), menuName));
        }
        return newConvertedMap;
    }

    private List<CocktailBean> convertAndCalculateAvailability(List<ICocktail> cocktails) {
        return cocktails
                .stream()
                .map(RestBeanConverter::toCocktailBean)
                .peek(availabilityCalculator::doUpdate)
                .collect(Collectors.toList());
    }

    //-------------------Retrieve a cocktail with details--------------------------------------------------------

    @Secured("ROLE_USER")
    @JsonView(View.CocktailWithDetails.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CocktailBean> getCocktail(@PathVariable("id") String id) {
        logger.info("Fetching cocktail with id " + id);

        ICocktail cocktail = cocktailsService.findCocktailById(id);
        CocktailBean cocktailBeanResponse = RestBeanConverter.toCocktailBean(cocktail);

        return new ResponseEntity<>(cocktailBeanResponse, HttpStatus.OK);
    }

    //-------------------Create a Cocktail--------------------------------------------------------

    @Secured("ROLE_USER")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CocktailBean> addCocktail(@RequestBody CocktailBean cocktailBean, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a new cocktail item " + cocktailBean);

        ICocktail saved = cocktailsService.saveCocktail(cocktailBean);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/cocktails/{id}").buildAndExpand(cocktailBean.getId()).toUri());
        CocktailBean cocktailBeanResponse = RestBeanConverter.toCocktailBean(saved);

        return new ResponseEntity<>(cocktailBeanResponse, headers, HttpStatus.CREATED);
    }

    //------------------- Update a Cocktail --------------------------------------------------------

    @Secured("ROLE_USER")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<CocktailBean> updateCocktail(@RequestBody CocktailBean cocktailBean) {
        logger.info("Updating a cocktail " + cocktailBean);

        ICocktail updated = cocktailsService.updateCocktail(cocktailBean);
        CocktailBean cocktailBeanResponse = RestBeanConverter.toCocktailBean(updated);

        return new ResponseEntity<>(cocktailBeanResponse, HttpStatus.OK);
    }

    //------------------- Delete a Cocktail --------------------------------------------------------

    @Secured("ROLE_USER")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<CocktailBean> deleteCocktail(@PathVariable("id") String id) {
        logger.info("Deleting a cocktail with id " + id);

        cocktailsService.deleteCocktailById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}