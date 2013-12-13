package mybar.app.managedbean.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mybar.app.managedbean.DishBean;
import mybar.entity.Category;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.Iterator;
import java.util.List;

@FacesConverter(forClass = Category.class, value = "categoryConverter")
public class CategoryConverter implements Converter {

    Logger logger = LoggerFactory.getLogger(CategoryConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        DishBean bean = (DishBean) context.getApplication().getELResolver().getValue(context.getELContext(), null, "dishBean");
        List<Category> categories = bean.getCategories();
        Iterator<Category> it = categories.iterator();
        //logger.info("as object: " + value);
        while (it.hasNext()) {
            Category ct = it.next();
            if (value.equals(ct.getName())) {
                //logger.info("as object: " + ct.toString());
                return ct;
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        //logger.info("as string: " + value.toString());
        return ((Category) value).getName();
    }
}