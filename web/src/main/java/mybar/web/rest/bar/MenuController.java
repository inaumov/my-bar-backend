package mybar.web.rest.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IMenu;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.Menu;
import mybar.app.bean.bar.View;
import mybar.service.bar.CocktailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private CocktailsService cocktailsService;

    //-------------------Retrieve Menu List--------------------------------------------------------

    @JsonView(View.Menu.class)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Menu>> listAllMenuItems() {
        logger.info("Fetching Menu items list...");
        List<IMenu> menuList = cocktailsService.getAllMenuItems();
        if (menuList.isEmpty()) {
            logger.info("Menu not found");
            return new ResponseEntity<List<Menu>>(HttpStatus.NOT_FOUND);
        }
        logger.info(MessageFormat.format("Found {0} items in menu list", menuList.size()));
        List<Menu> converted = new ArrayList<>();
        for (IMenu menu : menuList) {
            converted.add(Menu.from(menu));
        }
        return new ResponseEntity<List<Menu>>(converted, HttpStatus.OK);
    }

    //-------------------Retrieve All Cocktails For Menu--------------------------------------------------------

    @JsonView(View.Cocktail.class)
    @RequestMapping(value = "/{menuId}/cocktails", method = RequestMethod.GET)
    public ResponseEntity<List<CocktailBean>> findCocktailsForMenu(@PathVariable("menuId") Integer menuId) {
        logger.info("Fetching Cocktails For Menu with id {0}...", menuId);
        List<ICocktail> cocktails = cocktailsService.getAllCocktailsForMenu(menuId);
        if (cocktails.isEmpty()) {
            logger.error(MessageFormat.format("Cocktails list for Menu with id={0} does not exist", menuId));
            return new ResponseEntity<List<CocktailBean>>(HttpStatus.NOT_FOUND);
        }
        List<CocktailBean> cocktailBeans = new ArrayList<>();
        for (ICocktail cocktail : cocktailBeans) {
            cocktailBeans.add(CocktailBean.from(cocktail));
        }
        logger.info(MessageFormat.format("Found {0} cocktails for menu with id={1}", cocktails.size(), menuId));
        return new ResponseEntity<List<CocktailBean>>(cocktailBeans, HttpStatus.OK);
    }

    //-------------------Retrieve a cocktail with details--------------------------------------------------------

    @JsonView(View.CocktailWithDetails.class)
    @RequestMapping(value = "/{menuId}/cocktails/{cocktailId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CocktailBean> getCocktail(@PathVariable("menuId") Integer menuId, @PathVariable("cocktailId") Integer cocktailId) {
        logger.info("Fetching Cocktail with id " + cocktailId);
        ICocktail cocktail = cocktailsService.findCocktailById(cocktailId);
        if (cocktail == null) {
            logger.info("Cocktail with id " + cocktailId + " not found");
            return new ResponseEntity<CocktailBean>(HttpStatus.NOT_FOUND);
        }
        logger.info(MessageFormat.format("Loaded cocktail with id={0}", cocktailId));
        return new ResponseEntity<CocktailBean>(CocktailBean.from(cocktail), HttpStatus.OK);
    }

    //-------------------Create a Cocktail--------------------------------------------------------

    @RequestMapping(value = "/{menuId}/cocktails", method = RequestMethod.POST)
    public ResponseEntity<Void> addCocktail(@PathVariable("menuId") Integer menuId, @RequestBody CocktailBean cocktailBean, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a new Cocktail item " + cocktailBean.getName());
// TODO check if ID 0 and we not update
        if (cocktailsService.isCocktailExist(cocktailBean)) {
            logger.info("A Bottle " + cocktailBean.getName() + " already exists");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        cocktailsService.saveOrUpdateCocktail(cocktailBean);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/bottle/{id}").buildAndExpand(cocktailBean.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

}