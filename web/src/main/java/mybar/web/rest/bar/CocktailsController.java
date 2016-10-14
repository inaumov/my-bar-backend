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
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/cocktails")
public class CocktailsController {

    private Logger logger = LoggerFactory.getLogger(CocktailsController.class);

    @Autowired
    private CocktailsService cocktailsService;

    //-------------------Retrieve Menu List--------------------------------------------------------

    @JsonView(View.Menu.class)
    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public ResponseEntity<List<MenuBean>> listAllMenuItems() {
        logger.info("Fetching Menu items list...");
        List<IMenu> menuList = cocktailsService.getAllMenuItems();
        if (menuList.isEmpty()) {
            logger.info("Menu list is empty.");
            return new ResponseEntity<>(Collections.<MenuBean>emptyList(), HttpStatus.NOT_FOUND);
        }
        List<MenuBean> convertedList = new ArrayList<>();
        for (IMenu menu : menuList) {
            convertedList.add(MenuBean.from(menu));
        }
        logger.info(MessageFormat.format("Found {0} items in menu list", menuList.size()));
        return new ResponseEntity<>(convertedList, HttpStatus.OK);
    }

    //-------------------Retrieve All Cocktails For Menu--------------------------------------------------------

    @JsonView(View.Cocktail.class)
    @RequestMapping(value = "/{menuId}/all", method = RequestMethod.GET)
    public ResponseEntity<List<CocktailBean>> findCocktailsForMenu(@PathVariable("menuId") Integer menuId) {
        logger.info("Fetching cocktails for menu with id {0}...", menuId);
        List<ICocktail> cocktails = cocktailsService.getAllCocktailsForMenu(menuId);
        if (cocktails.isEmpty()) {
            logger.info(MessageFormat.format("Cocktails list for Menu with id={0} is empty.", menuId));
            return new ResponseEntity<>(Collections.<CocktailBean>emptyList(), HttpStatus.OK);
        }
        List<CocktailBean> convertedList = new ArrayList<>();
        for (ICocktail cocktail : cocktails) {
            convertedList.add(CocktailBean.from(cocktail));
        }
        logger.info(MessageFormat.format("Found {0} cocktails for menu with id={1}", cocktails.size(), menuId));
        return new ResponseEntity<>(convertedList, HttpStatus.OK);
    }

    //-------------------Retrieve a cocktail with details--------------------------------------------------------

    @JsonView(View.CocktailWithDetails.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CocktailBean> getCocktail(@PathVariable("id") Integer id) {
        logger.info("Fetching cocktail with id " + id);
        ICocktail cocktail = cocktailsService.findCocktailById(id);

        return new ResponseEntity<>(CocktailBean.from(cocktail), HttpStatus.OK);
    }

    //-------------------Create a Cocktail--------------------------------------------------------

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CocktailBean> addCocktail(@RequestBody CocktailBean cocktailBean, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a new cocktail item " + cocktailBean.getName());

        ICocktail saved = cocktailsService.saveOrUpdateCocktail(cocktailBean);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/cocktail/{id}").buildAndExpand(cocktailBean.getId()).toUri());
        return new ResponseEntity<>(CocktailBean.from(saved), headers, HttpStatus.CREATED);
    }

    //------------------- Update a Cocktail --------------------------------------------------------

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<CocktailBean> updateCocktail(@PathVariable("id") int id, @RequestBody CocktailBean cocktailBean) {
        logger.info("Updating a cocktail " + id);

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