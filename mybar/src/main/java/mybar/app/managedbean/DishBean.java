package mybar.app.managedbean;

import mybar.api.IBasis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mybar.ActiveStatus;
import mybar.DishType;
import mybar.api.ICategory;
import mybar.api.IDish;
import mybar.entity.Category;
import mybar.service.MenuManagementService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@ManagedBean(name = "dishBean")
@SessionScoped
public class DishBean implements IDish {
    Logger logger = LoggerFactory.getLogger(DishBean.class);

    @Autowired
    private MenuManagementService menuService;

    private int id;
    private ICategory category;
    private String name;
    private String description;
    private double price;
    private DishType dishType;
    private ActiveStatus status;
    private ArrayList<SelectItem> selectItems;
    private SelectItem selectItem;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public ICategory getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public DishType getDishType() {
        return dishType;
    }

    public void setDishType(DishType dishType) {
        this.dishType = dishType;
    }

    @Override
    public ActiveStatus getActiveStatus() {
        return status;
    }

    public void setActiveStatus(ActiveStatus status) {
        this.status = status;
    }


    public DishType[] getDishTypes() {
        return DishType.values();
    }
/*    public SelectItem[] getDishTypes() {
        SelectItem[] items = new SelectItem[DishType.values().length];
        int i = 0;
        for(DishType g: DishType.values()) {
            items[i++] = new SelectItem(g, g.name());
        }
        return items;
    }*/
    public ActiveStatus[] getActiveStatuses() {
        return ActiveStatus.values();
    }

    public void init() {
        IDish dish = menuService.findDish(id);
        this.id = dish.getId();
        this.category = dish.getCategory();
        this.name = dish.getName();
        this.description = dish.getDescription();
        this.price = dish.getPrice();
        this.dishType = dish.getDishType();
        this.status = dish.getActiveStatus();
        logger.info("init: " + dish.getName());
        getCategories();
        for(SelectItem si : selectItems) {
            if(si.getLabel().equals(category.getName())){
                selectItem = si;
            }
        }
    }

    public void save() {
        logger.info("save: " + this.name);
        menuService.saveOrUpdateDish(this);
    }

    public void remove() {
        try {
            menuService.removeDish(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Category> getCategories() {

        List<Category> allCategories = menuService.getAllCategories();
        selectItems = new ArrayList<SelectItem>();
        for (Category ctg : allCategories) {
            selectItems.add(new SelectItem(ctg, ctg.getName()));
            //logger.info(ctg.toString());
        }
        return allCategories;
    }

    public ArrayList<SelectItem> getSelectItems() {
        //logger.info(String.valueOf(selectItems.size()));
        return selectItems;
    }

    public SelectItem getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(SelectItem selectItem) {
        this.selectItem = selectItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean dishIsInHistory() {
        if(id != 0)
            return menuService.dishIsInHistory(this);
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

    @Override
    public Collection<? extends IBasis> getBasisList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Blob getPicture() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}