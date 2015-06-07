package mybar.web.rest;

import mybar.api.IMenu;
import mybar.app.bean.MenuBean;
import mybar.app.bean.DrinkBean;
import mybar.app.bean.DrinkList;
import mybar.app.bean.MenuList;
import mybar.service.MenuManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class MenuController {

    private Logger logger = LoggerFactory.getLogger(MenuController.class);

    private static final String XML_VIEW_NAME = "menu";

    @Autowired
    MenuManagementService menuManagementService;

    @Autowired
    private Jaxb2Marshaller menuMarshaller;

    private List<MenuBean> menus;

    // menus -> get all

    @RequestMapping(method = RequestMethod.GET, value = "/menu")
    public ModelAndView getMenus() {
        if (menus == null) {
            menus = toBeans(menuManagementService.getMenus());
        }
        return new ModelAndView(XML_VIEW_NAME, "menu", new MenuList(menus));
    }

    // drink -> create

    @RequestMapping(method = RequestMethod.POST, value = "/menu/{menuId}/drinks")
    public ModelAndView addDrink(@PathVariable("menuId") String menuId, @RequestBody String body) {
        Source source = new StreamSource(new StringReader(body));
        DrinkBean drink = (DrinkBean) menuMarshaller.unmarshal(source);
        MenuBean menu = menus.get(Integer.parseInt(menuId));
        drink.setMenu(menu);
        menuManagementService.saveOrUpdateDrink(drink);
        return new ModelAndView(XML_VIEW_NAME, "drink", drink);
    }

    // drink -> read all

    @RequestMapping(method = RequestMethod.GET, value = "/menu/{menuId}")
    public ModelAndView getDrinks(@PathVariable("menuId") String id) {
        int menuId = Integer.parseInt(id);
        if (menuId < 0 || menuId > 5) {
            logger.error(MessageFormat.format("Bad request. Menu with id={0} does not exist", menuId));
        }
        Collection<DrinkBean> drinks = menus.get(menuId).getDrinks();
        logger.info(MessageFormat.format("Loaded drinks for menu with id={0}", menuId));
        return new ModelAndView(XML_VIEW_NAME, "drinks", new DrinkList(drinks));
    }

    // drink -> read

    @RequestMapping(method = RequestMethod.GET, value = "/menu/{menuId}/drink/{drinkId}")
    public ModelAndView getDrink(@PathVariable("menuId") String firstId, @PathVariable("drinkId") String secondId) {
        int menuId = Integer.parseInt(firstId);
        if (menuId < 0 || menuId >= menus.size()) {
            logger.error(MessageFormat.format("Bad request. Menu with id={0} does not exist", menuId));
        }
        List<DrinkBean> drinks = menus.get(menuId).getDrinks();
        int drinkId = Integer.parseInt(firstId);
        if (drinkId < 0 || drinkId >= drinks.size()) {
            logger.error(MessageFormat.format("Bad request. Drink with id={0} does not exist", drinkId));
        }
        DrinkBean drink = drinks.get(drinkId);
        logger.info(MessageFormat.format("Loaded drink with id={0}", secondId));
        return new ModelAndView(XML_VIEW_NAME, "drink", drink);
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