package mybar.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import mybar.entity.Category;
import mybar.entity.Dish;
import mybar.service.MenuManagementService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class MenuController implements Serializable {

    Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    MenuManagementService menuManagementService;

    private int id;
    private String name;

    private Collection<Dish> menu;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void init() {
        if (id < 0 || id > 5) {
            String message = "Bad request. Please use a link from within the system.";

            return;
        }

        menu = menuManagementService.getAllCategories().get(id).getDishes();

        if (menu == null) {
            String message = "Bad request. Unknown user.";
        } else {
            logger.info("Loaded menu for category id=" + String.valueOf(id));
        }
    }

    public void addCategory(String name) {
        Category category = new Category();
        category.setName(name);
        menuManagementService.saveOrUpdateCategory(category);
    }

    public List<Category> getCategories() {
        return menuManagementService.getAllCategories();
    }

    public Collection<Dish> getDishes() {
        return menu;
    }

    public boolean isSelected(int index) {
        return id == index;
    }
}