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

    // drinks -> read all
    @JsonView(View.Drink.class)
    @RequestMapping(method = RequestMethod.GET, value = "/{menuId}/drinks", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<DrinkBean> getDrinks(@PathVariable("menuId") Integer menuId) {
        if (menuId < 0 || menuId > 5) {
            logger.error(MessageFormat.format("Bad request. Menu with id={0} does not exist", menuId));
        }
        Collection<DrinkBean> drinks = getMenus().get(menuId).getDrinks();
        logger.info(MessageFormat.format("Loaded drinks for menu with id={0}", menuId));
        return drinks;
    }

    // drinks -> read one by id with details
    //@JsonView(View.DrinkWithDetails.class)
    @RequestMapping(method = RequestMethod.GET, value = "/{menuId}/drinks/{drinkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MappingJacksonValue getDrink(@PathVariable("menuId") Integer menuId, @PathVariable("drinkId") Integer drinkId) {
        if (menuId < 0 || menuId >= getMenus().size()) {
            logger.error(MessageFormat.format("Bad request. Menu with id={0} does not exist", menuId));
        }
        List<DrinkBean> drinks = getMenus().get(menuId).getDrinks();
        if (drinkId < 0 || drinkId >= drinks.size()) {
            logger.error(MessageFormat.format("Bad request. Drink with id={0} does not exist", drinkId));
        }
        DrinkBean drink = drinks.get(drinkId);
        MappingJacksonValue wrapper = new MappingJacksonValue(drink);
        wrapper.setSerializationView(View.DrinkWithDetails.class);
        logger.info(MessageFormat.format("Loaded drink with id={0}", drinkId));
        return wrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{menuId}/drinks")
    public ResponseEntity<?> addDrink(@PathVariable("menuId") String menuId, @RequestBody DrinkBean input) {
        MenuBean menu = getMenus().get(Integer.parseInt(menuId));
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