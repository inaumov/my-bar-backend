package mybar.web.rest;

import mybar.api.IProduct;
import mybar.app.bean.ProductBean;
import mybar.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/storage")
public class StorageController {

    private Logger logger = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    private StorageService storageService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Collection<ProductBean> getAllProducts() {
        return toBeans(storageService.getAllBottles());
    }

    private static List<ProductBean> toBeans(List<IProduct> products) {
        List<ProductBean> menuBeans = new ArrayList<>();
        for (IProduct product : products) {
            menuBeans.add(ProductBean.from(product));
        }
        return menuBeans;
    }



}
