package mybar.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.IMenu;
import mybar.app.bean.CocktailBean;
import mybar.app.bean.MenuBean;
import mybar.app.bean.View;
import mybar.service.MenuManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private MenuManagementService menuManagementService;

    private List<MenuBean> menus;

    // menu -> get all
    @JsonView(View.Menu.class)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Collection<MenuBean> getAllMenuItems() {
        List<MenuBean> list = getMenus();
        logger.info(MessageFormat.format("Loaded {0} menus: ", list.size()));
        return list;
    }

    private List<MenuBean> getMenus() {
        if (menus == null) {
            menus = toBeans(menuManagementService.getMenus());
        }
        return menus;
    }

    // cocktails -> read all
    @JsonView(View.Cocktail.class)
    @RequestMapping(method = RequestMethod.GET, value = "/{menuId}/cocktails", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<CocktailBean> getCocktails(@PathVariable("menuId") Integer menuId) {
        if (menuId < 0 || menuId > 5) {
            logger.error(MessageFormat.format("Bad request. Menu with id={0} does not exist", menuId));
        }
        Collection<CocktailBean> cocktails = getMenus().get(menuId).getCocktails();
        logger.info(MessageFormat.format("Loaded cocktails for menu with id={0}", menuId));
        return cocktails;
    }

    // cocktails -> read one by id with details
    //@JsonView(View.CocktailWithDetails.class)
    @RequestMapping(method = RequestMethod.GET, value = "/{menuId}/cocktails/{cocktailId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MappingJacksonValue getCocktail(@PathVariable("menuId") Integer menuId, @PathVariable("cocktailId") Integer cocktailId) {
        if (menuId < 0 || menuId >= getMenus().size()) {
            logger.error(MessageFormat.format("Bad request. Menu with id={0} does not exist", menuId));
        }
        List<CocktailBean> cocktails = getMenus().get(menuId).getCocktails();
        if (cocktailId < 0 || cocktailId >= cocktails.size()) {
            logger.error(MessageFormat.format("Bad request. Cocktail with id={0} does not exist", cocktailId));
        }
        CocktailBean cocktail = cocktails.get(cocktailId);
        MappingJacksonValue wrapper = new MappingJacksonValue(cocktail);
        wrapper.setSerializationView(View.CocktailWithDetails.class);
        logger.info(MessageFormat.format("Loaded cocktail with id={0}", cocktailId));
        return wrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{menuId}/cocktails")
    public ResponseEntity<?> addCocktail(@PathVariable("menuId") String menuId, @RequestBody CocktailBean input) {
        MenuBean menu = getMenus().get(Integer.parseInt(menuId));
        input.setMenu(menu);
        menuManagementService.saveOrUpdateCocktail(input);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(input.getId()).toUri());

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    // menu -> update
    // TODO
    // menu -> delete
    // TODO

    private static List<MenuBean> toBeans(List<IMenu> menus) {
        List<MenuBean> menuBeans = new ArrayList<>();
        for (IMenu menu : menus) {
            menuBeans.add(MenuBean.from(menu));
        }
        return menuBeans;
    }

}