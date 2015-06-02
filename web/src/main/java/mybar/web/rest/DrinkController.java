package mybar.web.rest;

import mybar.api.IBasis;
import mybar.api.IDrink;
import mybar.entity.Category;
import mybar.service.MenuManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Blob;
import java.util.Collection;
import java.util.List;

public class DrinkController {
    Logger logger = LoggerFactory.getLogger(DrinkController.class);

    @Autowired
    private MenuManagementService menuService;

    public void get(int id) {
        IDrink drink = menuService.findDrink(id);
        logger.info("init: " + drink.getName());
        getCategories();
    }

    public void save() {

        //menuService.saveOrUpdateDrink(new Drink());
    }

    public void remove() {
        try {
            //menuService.removeDrink(drink);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Category> getCategories() {

        List<Category> allCategories = menuService.getAllCategories();
        return allCategories;
    }

    public boolean drinkIsInHistory() {
        //if(id != 0)
            //return menuService.drinkIsInHistory(this);
        return false;
    }

/*    private Part uploadedImage;

    public void upload(){
        logger.info("call upload...");
        logger.info("content-type:{0}" + uploadedImage.getContentType());
        logger.info("filename:{0}" + uploadedImage.getName());
        logger.info("submitted filename:{0}" + uploadedImage.getName());
        logger.info("size:{0}" + uploadedImage.getSize());
        try {
            byte[] results=new byte[(int) uploadedImage.getSize()];
            InputStream in= uploadedImage.getInputStream();
            in.read(results);
        } catch (IOException ex) {
            logger.info(" ex @{0}", ex);
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uploaded!"));
    }

    public Part getUploadedImage() {
        return uploadedImage;
    }

    public void setUploadedImage(Part uploadedImage) {
        this.uploadedImage = uploadedImage;
    }*/

    public Collection<? extends IBasis> getBasisList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Blob getPicture() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}