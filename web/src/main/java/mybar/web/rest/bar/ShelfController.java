package mybar.web.rest.bar;

import mybar.api.bar.IBottle;
import mybar.app.bean.bar.BottleBean;
import mybar.service.bar.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shelf")
public class ShelfController {

    private Logger logger = LoggerFactory.getLogger(ShelfController.class);

    @Autowired
    private StorageService storageService;

    //-------------------Retrieve All Bottles--------------------------------------------------------

    @RequestMapping(value = "/bottles", method = RequestMethod.GET)
    public ResponseEntity<List<BottleBean>> listAllBottles() {
        List<IBottle> allBottles = storageService.findAllBottles();
        if (allBottles.isEmpty()) {
            return new ResponseEntity<List<BottleBean>>(HttpStatus.NOT_FOUND);
        }
        List<BottleBean> beans = new ArrayList<>();
        for (IBottle bottle : allBottles) {
            beans.add(BottleBean.from(bottle));
        }
        return new ResponseEntity<List<BottleBean>>(beans, HttpStatus.OK);
    }

    //-------------------Retrieve a Bottle--------------------------------------------------------

    @RequestMapping(value = "/bottles/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BottleBean> getBottle(@PathVariable("id") int id) {
        logger.info("Fetching Bottle with id " + id);
        IBottle bottle = storageService.findById(id);
        if (bottle == null) {
            logger.info("Bottle with id " + id + " not found");
            return new ResponseEntity<BottleBean>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BottleBean>(BottleBean.from(bottle), HttpStatus.OK);
    }

    //-------------------Create a Bottle--------------------------------------------------------

    @RequestMapping(value = "/bottles", method = RequestMethod.POST)
    public ResponseEntity<Void> createBottle(@RequestBody BottleBean bottleBean, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a Bottle " + bottleBean.getBeverageId());

        boolean saved = storageService.saveBottle(bottleBean);
        if (!saved) {
            logger.info("A Bottle " + bottleBean.getBeverageId() + " already exists");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/bottle/{id}").buildAndExpand(bottleBean.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    //------------------- Update a Bottle --------------------------------------------------------

    @RequestMapping(value = "/bottles/{id}", method = RequestMethod.PUT)
    public ResponseEntity<BottleBean> updateBottle(@PathVariable("id") int id, @RequestBody BottleBean bottleBean) {
        logger.info("Updating Bottle " + id);

        IBottle bottle = storageService.findById(id);

        if (bottle == null) {
            logger.info("Bottle with id " + id + " not found");
            return new ResponseEntity<BottleBean>(HttpStatus.NOT_FOUND);
        }

        storageService.updateBottle(bottleBean);
        return new ResponseEntity<BottleBean>(bottleBean, HttpStatus.OK);
    }

    //------------------- Delete a Bottle --------------------------------------------------------

    @RequestMapping(value = "/bottles/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<BottleBean> deleteBottle(@PathVariable("id") int id) {
        logger.info("Fetching & Deleting Bottle with id " + id);

        IBottle bottle = storageService.findById(id);
        if (bottle == null) {
            logger.info("Unable to delete. Bottle with id " + id + " not found");
            return new ResponseEntity<BottleBean>(HttpStatus.NOT_FOUND);
        }

        storageService.deleteBottleById(id);
        return new ResponseEntity<BottleBean>(HttpStatus.NO_CONTENT);
    }

    //------------------- Delete All Bottles --------------------------------------------------------

    @RequestMapping(value = "/bottles", method = RequestMethod.DELETE)
    public ResponseEntity<BottleBean> deleteAllBottles() {
        logger.info("Deleting All Bottles");

        int numberOfBottles = storageService.deleteAllBottles();
        // todo return number
        return new ResponseEntity<BottleBean>(HttpStatus.NO_CONTENT);
    }

}