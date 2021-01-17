package mybar.service.bar;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import mybar.api.bar.IBottle;
import mybar.api.bar.ingredient.IBeverage;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.dto.DtoFactory;
import mybar.dto.bar.BottleDto;
import mybar.exception.BottleNotFoundException;
import mybar.exception.UnknownBeverageException;
import mybar.repository.bar.BottleDao;
import mybar.repository.bar.IngredientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static mybar.dto.DtoFactory.toDto;

@Service
@Transactional
public class ShelfService {

    private BottleDao bottleDao;

    private IngredientDao ingredientDao;

    private Cache<String, IBottle> bottlesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    @Autowired
    public ShelfService(BottleDao bottleDao, IngredientDao ingredientDao) {
        this.bottleDao = bottleDao;
        this.ingredientDao = ingredientDao;
    }

    public IBottle findById(final String id) throws BottleNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "Bottle id is required.");
        IBottle present = bottlesCache.getIfPresent(id);
        if (present != null) {
            return present;
        }
        present = loadById(id);
        bottlesCache.put(present.getId(), present);
        return present;
    }

    private BottleDto loadById(String id) {
        Bottle read = bottleDao.read(id);
        if (read != null) {
            return DtoFactory.toDto(read);
        }
        throw new BottleNotFoundException(id);
    }

    public IBottle saveBottle(IBottle bottle) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bottle.getBrandName()), "Brand name is required.");
        Preconditions.checkArgument(bottle.getBeverage() != null && bottle.getBeverage().getId() >= 0, "Beverage ID is required.");
        checkBeverageExists(bottle.getBeverage());

        Bottle newEntity = EntityFactory.from(bottle);
        newEntity.setBeverage(getBeverageById(bottle.getBeverage()));

        try {
            Bottle entity = bottleDao.create(newEntity);
            BottleDto dto = toDto(entity);
            clearCache();
            return dto;
        } catch (EntityExistsException e) {
            return null;
        }
    }

    public IBottle updateBottle(IBottle bottle) throws BottleNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bottle.getId()), "Bottle id is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bottle.getBrandName()), "Brand name is required.");
        Preconditions.checkArgument(bottle.getBeverage() != null && bottle.getBeverage().getId() >= 0, "Beverage ID is required.");
        checkBeverageExists(bottle.getBeverage());

        Bottle newEntity = EntityFactory.from(bottle);
        newEntity.setBeverage(getBeverageById(bottle.getBeverage()));
        Bottle entity = bottleDao.update(newEntity);
        clearCache();
        return toDto(entity);
    }

    private void checkBeverageExists(IBeverage beverage) throws UnknownBeverageException {
        if (getBeverageById(beverage) == null) {
            throw new UnknownBeverageException(beverage);
        }
    }

    private Beverage getBeverageById(IBeverage beverage) {
        return ingredientDao.findBeverageById(beverage.getId());
    }

    public void deleteBottleById(final String id) throws BottleNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "Bottle id is required.");
        try {
            bottleDao.delete(id);
        } catch (EntityNotFoundException e) {
            return; // TODO: 1/12/2018 handle properly
        }
        bottlesCache.invalidate(id);
    }

    public List<IBottle> findAllBottles() {
        ensureAllBottlesLoaded();
        return new ArrayList<>(bottlesCache.asMap().values());
    }

    private void ensureAllBottlesLoaded() {
        if (bottlesCache.size() == 0) {
            loadAllBottles();
        }
    }

    private void loadAllBottles() {
        Map<String, BottleDto> allBottles = bottleDao.findAll()
                .stream()
                .collect(Collectors.toMap(Bottle::getId, DtoFactory::toDto));
        bottlesCache.putAll(allBottles);
    }

    public int deleteAllBottles() {
        int i = bottleDao.destroyAll();
        if (i > 0) {
            clearCache();
        }
        return i;
    }

    public boolean isBottleAvailable(final int ingredientId) {
        ensureAllBottlesLoaded();
        Optional<IBottle> bottleOptional = bottlesCache.asMap().values()
                .stream()
                .filter(bottle -> {
                    IBeverage beverage = bottle.getBeverage();
                    return beverage != null && beverage.getId() == ingredientId;
                })
                .findAny();
        return bottleOptional.isPresent() && bottleOptional.get().isInShelf();
    }

    private void clearCache() {
        bottlesCache.cleanUp();
    }

}