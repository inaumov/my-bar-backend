package mybar.web.rest.bar;

import lombok.extern.slf4j.Slf4j;
import mybar.api.bar.IMenu;
import mybar.app.RestBeanConverter;
import mybar.app.bean.bar.MenuBean;
import mybar.service.bar.CocktailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.*;

@RestController
@RequestMapping("/menu")
@Slf4j
public class MenuController {

    private final CocktailsService cocktailsService;
    private final MessageSource messageSource;

    @Autowired
    public MenuController(CocktailsService cocktailsService, MessageSource messageSource) {
        this.cocktailsService = cocktailsService;
        this.messageSource = messageSource;
    }

    //-------------------Retrieve Menu List--------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MenuBean>> listAllMenuItems() {
        log.info("Fetching menu items list...");

        Collection<IMenu> allMenuItems = cocktailsService.getAllMenuItems();
        if (allMenuItems.isEmpty()) {
            log.info("Menu list is empty.");
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        // converted into response beans
        List<MenuBean> menuBeans = convertWithTranslations(allMenuItems);
        for (MenuBean menuBean : menuBeans) {
            Link link = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(CocktailsController.class)
            .allCocktails(menuBean.getName())).withRel("allCocktails");
            menuBean.add(link);
        }
        return new ResponseEntity<>(menuBeans, HttpStatus.OK);
    }

    private List<MenuBean> convertWithTranslations(Collection<IMenu> menuItems) {
        List<MenuBean> convertedList = new ArrayList<>();
        Locale locale = LocaleContextHolder.getLocale();
        for (IMenu menu : menuItems) {
            MenuBean from = RestBeanConverter.toMenuBean(menu);
            from.setTranslation(messageSource.getMessage(menu.getName(), null, locale));
            convertedList.add(from);
        }
        log.info(MessageFormat.format("Found {0} items in menu list.", menuItems.size()));
        return convertedList;
    }

}