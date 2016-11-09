package mybar.web.rest.bar;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IMenu;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.MenuBean;
import mybar.app.bean.bar.View;
import mybar.service.bar.CocktailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.*;

@RestController
@RequestMapping("/cocktails")
public class CocktailsController {

    private Logger logger = LoggerFactory.getLogger(CocktailsController.class);

    @Autowired
    private CocktailsService cocktailsService;
    @Autowired
    private MessageSource messageSource;

    //-------------------Retrieve Menu List--------------------------------------------------------

    @JsonView(View.Menu.class)
    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public ResponseEntity<List<MenuBean>> listAllMenuItems() {
        logger.info("Fetching menu items list...");

        List<IMenu> menuList = cocktailsService.getAllMenuItems();
        if (menuList.isEmpty()) {
            logger.info("Menu list is empty.");
            return new ResponseEntity<>(Collections.<MenuBean>emptyList(), HttpStatus.OK);
        }
        List<MenuBean> convertedList = new ArrayList<>();
        Locale locale = LocaleContextHolder.getLocale();
        for (IMenu menu : menuList) {
            MenuBean from = MenuBean.from(menu);
            from.setTranslation(messageSource.getMessage(menu.getName(), null, locale));
            convertedList.add(from);
        }
        logger.info(MessageFormat.format("Found {0} items in menu list.", menuList.size()));
        return new ResponseEntity<>(convertedList, HttpStatus.OK);
    }

    //-------------------Retrieve All Cocktails For Menu--------------------------------------------------------

    @JsonView(View.Cocktail.class)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<CocktailBean>>> allCocktails() {
        Map<String, List<ICocktail>> cocktails = cocktailsService.getAllCocktails();
        if (cocktails.isEmpty()) {
            logger.info("Cocktail list is empty.");
            return new ResponseEntity<>(Collections.<String, List<CocktailBean>>emptyMap(), HttpStatus.OK);
        }
        Map<String, List<CocktailBean>> converted = Maps.newHashMap();
        for (String menuName : cocktails.keySet()) {
            List<CocktailBean> cocktailBeans = Lists.newArrayList();
            for (ICocktail cocktail : cocktails.get(menuName)) {
                cocktailBeans.add(CocktailBean.from(cocktail));
            }
            converted.put(menuName, cocktailBeans);
        }
        for (String menu : converted.keySet()) {
            logger.info(MessageFormat.format("Found {0} cocktails for menu with id={1}", converted.get(menu).size(), menu));
        }
        return new ResponseEntity<>(converted, HttpStatus.OK);
    }

    //-------------------Retrieve a cocktail with details--------------------------------------------------------

    @JsonView(View.CocktailWithDetails.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CocktailBean> getCocktail(@PathVariable("id") Integer id) {
        logger.info("Fetching cocktail with id " + id);

        ICocktail cocktail = cocktailsService.findCocktailById(id);
        return new ResponseEntity<>(CocktailBean.from(cocktail), HttpStatus.OK);
    }

    //-------------------Create a Cocktail--------------------------------------------------------

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CocktailBean> addCocktail(@RequestBody CocktailBean cocktailBean, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a new cocktail item " + cocktailBean);

        ICocktail saved = cocktailsService.saveOrUpdateCocktail(cocktailBean);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/cocktails/{id}").buildAndExpand(cocktailBean.getId()).toUri());
        return new ResponseEntity<>(CocktailBean.from(saved), headers, HttpStatus.CREATED);
    }

    //------------------- Update a Cocktail --------------------------------------------------------

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<CocktailBean> updateCocktail(@RequestBody CocktailBean cocktailBean) {
        logger.info("Updating a cocktail " + cocktailBean);

        ICocktail updated = cocktailsService.saveOrUpdateCocktail(cocktailBean);
        return new ResponseEntity<>(CocktailBean.from(updated), HttpStatus.OK);
    }

    //------------------- Delete a Cocktail --------------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<CocktailBean> deleteCocktail(@PathVariable("id") int id) {
        logger.info("Deleting a cocktail with id " + id);

        cocktailsService.deleteCocktailById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}