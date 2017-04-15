package mybar.dto.bar;

import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.dto.DtoFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BottleTest {

    public static final int TEST_ID = 1;
    public static final String BRAND_NAME = "brand name";
    public static final double PRICE = 100;
    public static final double VOLUME = 1;
    public static final String IMAGE_URL = "http://bottle-image.jpg";

    @Test
    public void testConvertBottleToDto() throws Exception {
        Bottle bottleEntity = new Bottle();
        bottleEntity.setId(TEST_ID);
        Beverage beverageEntity = new Beverage();
        beverageEntity.setId(TEST_ID);
        bottleEntity.setBeverage(beverageEntity);
        bottleEntity.setBrandName(BRAND_NAME);
        bottleEntity.setInShelf(true);
        bottleEntity.setPrice(PRICE);
        bottleEntity.setVolume(VOLUME);
        bottleEntity.setImageUrl(IMAGE_URL);

        BottleDto dto = DtoFactory.toDto(bottleEntity);
        assertEquals(TEST_ID, dto.getId());
        assertEquals(TEST_ID, dto.getBeverage().getId());
        assertEquals(BRAND_NAME, dto.getBrandName());
        assertEquals(true, dto.isInShelf());
        assertEquals(PRICE, dto.getPrice(), 0);
        assertEquals(VOLUME, dto.getVolume(), 0);
        assertEquals(IMAGE_URL, dto.getImageUrl());
    }

}