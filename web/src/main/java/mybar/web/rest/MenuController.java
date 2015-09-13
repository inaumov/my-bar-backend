package mybar.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.IMenu;
import mybar.app.bean.DrinkBean;
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

    // menus -> get all
    @JsonView(View.Menu.class)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Collection<MenuBean> getAllMenuItems() {
        if (menus == null) {
            menus = toBeans(menuManagementService.getMenus());
        }
        logger.info(MessageFormat.format("Loaded {0} menus: ", menus.size()));
        return menus;
    }

    // drink -> read all
    @JsonView(View.Drink.class)
    @RequestMapping(method = RequestMethod.GET, value = "/{menuId}/drinks", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<DrinkBean> getDrinks(@PathVariable("menuId") Integer menuId) {
        if (menuId < 0 || menuId > 5) {
            logger.error(MessageFormat.format("Bad request. Menu with id={0} does not exist", menuId));
        }
        Collection<DrinkBean> drinks = menus.get(menuId).getDrinks();
        logger.info(MessageFormat.format("Loaded drinks for menu with id={0}", menuId));
        return drinks;
    }

    // drink -> read
    @JsonView(View.DrinkWithDetails.class)
    @RequestMapping(value = "/{menuId}/drinks/{drinkId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DrinkBean getDrink(@PathVariable("menuId") Integer menuId, @PathVariable("drinkId") Integer drinkId) {
        if (menuId < 0 || menuId >= menus.size()) {
            logger.error(MessageFormat.format("Bad request. Menu with id={0} does not exist", menuId));
        }
        List<DrinkBean> drinks = menus.get(menuId).getDrinks();
        if (drinkId < 0 || drinkId >= drinks.size()) {
            logger.error(MessageFormat.format("Bad request. Drink with id={0} does not exist", drinkId));
        }
        DrinkBean drink = drinks.get(drinkId);
        logger.info(MessageFormat.format("Loaded drink with id={0}", drinkId));
        return drink;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{menuId}/drinks")
    public ResponseEntity<?> addDrink(@PathVariable("menuId") String menuId, @RequestBody DrinkBean input) {
        MenuBean menu = menus.get(Integer.parseInt(menuId));
        input.setMenu(menu);
        menuManagementService.saveOrUpdateDrink(input);

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