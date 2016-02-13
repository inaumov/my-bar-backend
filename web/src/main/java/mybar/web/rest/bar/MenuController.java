package mybar.web.rest.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IMenu;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.MenuBean;
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
    public ResponseEntity<List<MenuBean>> listAllMenuItems() {
        logger.info("Fetching Menu items list...");
        List<IMenu> menuList = cocktailsService.getAllMenuItems();
        if (menuList.isEmpty()) {
            logger.info("Menu not found");
            return new ResponseEntity<List<MenuBean>>(HttpStatus.NOT_FOUND);
        }
        logger.info(MessageFormat.format("Found {0} items in menu list", menuList.size()));
        List<MenuBean> converted = new ArrayList<>();
        for (IMenu menu : menuList) {
            converted.add(MenuBean.from(menu));
        }
        return new ResponseEntity<List<MenuBean>>(converted, HttpStatus.OK);
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
        for (ICocktail cocktail : cocktails) {
            cocktailBeans.add(CocktailBean.from(cocktail));
        }
        logger.info(MessageFormat.format("Found {0} cocktails for menu with id={1}", cocktails.size(), menuId));
        return new ResponseEntity<List<CocktailBean>>(cocktailBeans, HttpStatus.OK);
    }

    //-------------------Retrieve a cocktail with details--------------------------------------------------------

    @JsonView(View.CocktailWithDetails.class)
    @RequestMapping(value = "/cocktails/{cocktailId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CocktailBean> getCocktail(@PathVariable("cocktailId") Integer cocktailId) {
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

    @RequestMapping(value = "/cocktails", method = RequestMethod.POST)
    public ResponseEntity<CocktailBean> addCocktail(@RequestBody CocktailBean cocktailBean, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a new Cocktail item " + cocktailBean.getName());

        ICocktail saved = cocktailsService.saveOrUpdateCocktail(cocktailBean);
        if (saved == null) {
            logger.info("A Bottle " + cocktailBean.getName() + " already exists");
            return new ResponseEntity<CocktailBean>(HttpStatus.CONFLICT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/bottle/{id}").buildAndExpand(cocktailBean.getId()).toUri());
        return new ResponseEntity<CocktailBean>(CocktailBean.from(saved), headers, HttpStatus.CREATED);
    }

    //------------------- Update a Cocktail --------------------------------------------------------

    @RequestMapping(value = "/cocktails/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CocktailBean> updateCocktail(@PathVariable("id") int id, @RequestBody CocktailBean cocktailBean) {
        ICocktail cocktail = cocktailsService.findCocktailById(id);
        if (cocktail == null) {
            logger.info("Bottle with id " + id + " not found");
            return new ResponseEntity<CocktailBean>(HttpStatus.NOT_FOUND);
        }
        logger.info("Updating Cocktail " + id);
        ICocktail updated = cocktailsService.saveOrUpdateCocktail(cocktailBean);
        return new ResponseEntity<CocktailBean>(CocktailBean.from(updated), HttpStatus.OK);
    }

    //------------------- Delete a Cocktail --------------------------------------------------------

    @RequestMapping(value = "/cocktails/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<CocktailBean> deleteCocktail(@PathVariable("id") int id) {
        logger.info("Fetching & Deleting Cocktail with id " + id);

        ICocktail cocktail = cocktailsService.findCocktailById(id);
        if (cocktail == null) {
            logger.info("Unable to delete. Cocktail with id " + id + " not found");
            return new ResponseEntity<CocktailBean>(HttpStatus.NOT_FOUND);
        }

        cocktailsService.deleteCocktailById(id);
        return new ResponseEntity<CocktailBean>(HttpStatus.NO_CONTENT);
    }

}