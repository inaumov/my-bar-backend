package mybar.web.rest.bar;

import mybar.api.bar.IProduct;
import mybar.app.bean.bar.Bottle;
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
    public ResponseEntity<List<Bottle>> listAllBottles() {
        List<IProduct> allBottles = storageService.findAllBottles();
        if (allBottles.isEmpty()) {
            return new ResponseEntity<List<Bottle>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Bottle>>(toBeans(allBottles), HttpStatus.OK);
    }

    //-------------------Retrieve a Bottle--------------------------------------------------------

    @RequestMapping(value = "/bottles/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bottle> getBottle(@PathVariable("id") int id) {
        logger.info("Fetching Bottle with id " + id);
        IProduct bottle = storageService.findById(id);
        if (bottle == null) {
            logger.info("Bottle with id " + id + " not found");
            return new ResponseEntity<Bottle>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Bottle>(Bottle.from(bottle), HttpStatus.OK);
    }

    //-------------------Create a Bottle--------------------------------------------------------

    @RequestMapping(value = "/bottles", method = RequestMethod.POST)
    public ResponseEntity<Void> createBottle(@RequestBody Bottle bottle, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a Bottle " + bottle.getBeverageKind());

        if (storageService.isBottleExist(bottle)) {
            logger.info("A Bottle " + bottle.getBeverageKind() + " already exists");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        storageService.saveBottle(bottle);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/bottle/{id}").buildAndExpand(bottle.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    //------------------- Update a Bottle --------------------------------------------------------

    @RequestMapping(value = "/bottles/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Bottle> updateBottle(@PathVariable("id") int id, @RequestBody Bottle bottle) {
        logger.info("Updating Bottle " + id);

        IProduct product = storageService.findById(id);

        if (product == null) {
            logger.info("Bottle with id " + id + " not found");
            return new ResponseEntity<Bottle>(HttpStatus.NOT_FOUND);
        }
        Bottle currentBottle = Bottle.from(product);
        currentBottle.setBeverageKind(bottle.getBeverageKind());
        currentBottle.setBrandName(bottle.getBrandName());
        currentBottle.setVolume(bottle.getVolume());
        currentBottle.setPrice(bottle.getPrice());

        storageService.updateBottle(currentBottle);
        return new ResponseEntity<Bottle>(currentBottle, HttpStatus.OK);
    }

    //------------------- Delete a Bottle --------------------------------------------------------

    @RequestMapping(value = "/bottles/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Bottle> deleteBottle(@PathVariable("id") int id) {
        logger.info("Fetching & Deleting Bottle with id " + id);

        IProduct product = storageService.findById(id);
        if (product == null) {
            logger.info("Unable to delete. Bottle with id " + id + " not found");
            return new ResponseEntity<Bottle>(HttpStatus.NOT_FOUND);
        }

        storageService.deleteBottleById(id);
        return new ResponseEntity<Bottle>(HttpStatus.NO_CONTENT);
    }

    //------------------- Delete All Bottles --------------------------------------------------------

    @RequestMapping(value = "/bottles", method = RequestMethod.DELETE)
    public ResponseEntity<Bottle> deleteAllBottles() {
        logger.info("Deleting All Bottles");

        storageService.deleteAllBottles();
        return new ResponseEntity<Bottle>(HttpStatus.NO_CONTENT);
    }

    private static List<Bottle> toBeans(List<IProduct> products) {
        List<Bottle> beans = new ArrayList<>();
        for (IProduct product : products) {
            beans.add(Bottle.from(product));
        }
        return beans;
    }

}