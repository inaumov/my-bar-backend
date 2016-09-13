package mybar.web.rest.bar;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import mybar.api.bar.IBottle;
import mybar.app.bean.bar.BottleBean;
import mybar.app.bean.bar.View;
import mybar.exception.BottleNotFoundException;
import mybar.service.bar.ShelfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/shelf")
public class ShelfController {

    private Logger logger = LoggerFactory.getLogger(ShelfController.class);

    @Autowired
    private ShelfService shelfService;

    //-------------------Retrieve All Bottles--------------------------------------------------------

    @RequestMapping(value = "/bottles", method = RequestMethod.GET)
    public ResponseEntity listAllBottles() {

        List<IBottle> allBottles = shelfService.findAllBottles();
        if (allBottles.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        List<BottleBean> beans = Lists.transform(allBottles, new Function<IBottle, BottleBean>() {
            @Override
            public BottleBean apply(IBottle bottle) {
                return BottleBean.from(bottle);
            }
        });

        MappingJacksonValue wrapper = new MappingJacksonValue(beans);
        wrapper.setSerializationView(View.Shelf.class);
        return new ResponseEntity(wrapper, HttpStatus.OK);
    }

    //-------------------Retrieve a Bottle--------------------------------------------------------

    @RequestMapping(value = "/bottles/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BottleBean> getBottle(@PathVariable("id") int id) {
        logger.info("Fetching Bottle with id " + id);
        IBottle bottle = shelfService.findById(id);
        if (bottle == null) {
            logger.info("Bottle with id " + id + " not found");
            return new ResponseEntity<BottleBean>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BottleBean>(BottleBean.from(bottle), HttpStatus.OK);
    }

    //-------------------Create a Bottle--------------------------------------------------------

    @RequestMapping(value = "/bottles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BottleBean> createBottle(@RequestBody BottleBean bottleBean, UriComponentsBuilder ucBuilder) {
//        logger.info("Creating a Bottle of " + bottleBean.getBeverage().getKind());
        // TODO check this
        IBottle saved = shelfService.saveBottle(bottleBean);
//        if (!saved) {
//            logger.info("A Bottle " + bottleBean.getBeverage().getKind() + " already exists");
//            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
//        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/bottle/{id}").buildAndExpand(bottleBean.getId()).toUri());
        return new ResponseEntity<BottleBean>(BottleBean.from(saved), headers, HttpStatus.CREATED);
    }

    //------------------- Update a Bottle --------------------------------------------------------

    @RequestMapping(value = "/bottles/{id}", method = RequestMethod.PUT)
    public ResponseEntity<BottleBean> updateBottle(@PathVariable("id") int id, @RequestBody BottleBean bottleBean) throws BottleNotFoundException {
        logger.info("Updating Bottle " + id);

        final IBottle updated = shelfService.updateBottle(bottleBean);
        return new ResponseEntity<BottleBean>(BottleBean.from(updated), HttpStatus.OK);
    }

    //------------------- Delete a Bottle --------------------------------------------------------

    @RequestMapping(value = "/bottles/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<BottleBean> deleteBottle(@PathVariable("id") int id) {
        logger.info("Fetching & Deleting Bottle with id " + id);

        shelfService.deleteBottleById(id);
        return new ResponseEntity<BottleBean>(HttpStatus.NO_CONTENT);
    }

    //------------------- Delete All Bottles --------------------------------------------------------

    @RequestMapping(value = "/bottles", method = RequestMethod.DELETE)
    public ResponseEntity<BottleBean> deleteAllBottles() {
        logger.info("Deleting All Bottles");

        shelfService.deleteAllBottles();
        return new ResponseEntity<BottleBean>(HttpStatus.NO_CONTENT);
    }

}