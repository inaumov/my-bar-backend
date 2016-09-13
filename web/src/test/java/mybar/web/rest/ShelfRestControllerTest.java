package mybar.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import mybar.BeverageType;
import mybar.api.bar.IBottle;
import mybar.app.bean.bar.BottleBean;
import mybar.dto.bar.BottleDto;
import mybar.dto.bar.ingredient.BeverageDto;
import mybar.exception.BottleNotFoundException;
import mybar.service.bar.ShelfService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-rest-context.xml")
public class ShelfRestControllerTest {

    public static final int TEST_ID_1 = 1;
    public static final String BRAND_NAME_1 = "brand name 1";
    public static final double PRICE_1 = 100;
    public static final double VOLUME_1 = 1;
    public static final String IMAGE_URL_1 = "http://bottle-image1.jpg";

    public static final int TEST_ID_2 = 2;
    public static final String BRAND_NAME_2 = "brand name 2";
    public static final double PRICE_2 = 200;
    public static final double VOLUME_2 = 0.5;
    public static final String IMAGE_URL_2 = "http://bottle-image2.jpg";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ShelfService shelfService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
        reset(shelfService);
    }

    @Test
    public void findAll_ShouldReturnAllBottleEntriesWithoutIngredients() throws Exception {

        final BottleDto first = new BottleDto();
        first.setId(TEST_ID_1);
        first.setBrandName(BRAND_NAME_1);
        first.setPrice(PRICE_1);
        first.setInShelf(true);
        first.setVolume(VOLUME_1);
        first.setImageUrl(IMAGE_URL_1);

        final BottleDto second = new BottleDto();
        second.setId(2);
        second.setBrandName(BRAND_NAME_2);
        second.setPrice(PRICE_2);
        second.setInShelf(false);
        second.setVolume(VOLUME_2);
        second.setImageUrl(IMAGE_URL_2);

        when(shelfService.findAllBottles()).thenReturn(Arrays.<IBottle>asList(first, second));

        mockMvc.perform(get("/shelf/bottles").accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id", is(TEST_ID_1)))
                .andExpect(jsonPath("$[0].brandName", is(BRAND_NAME_1)))
                .andExpect(jsonPath("$[0].price", is(PRICE_1)))
                .andExpect(jsonPath("$[0].inShelf", is(true)))
                .andExpect(jsonPath("$[0].volume", is(VOLUME_1)))
                .andExpect(jsonPath("$[0].imageUrl", is(IMAGE_URL_1)))

                .andExpect(jsonPath("$[1].id", is(TEST_ID_2)))
                .andExpect(jsonPath("$[1].brandName", is(BRAND_NAME_2)))
                .andExpect(jsonPath("$[1].price", is(PRICE_2)))
                .andExpect(jsonPath("$[1].inShelf", is(false)))
                .andExpect(jsonPath("$[1].volume", is(VOLUME_2)))
                .andExpect(jsonPath("$[1].imageUrl", is(IMAGE_URL_2)));

        verify(shelfService, times(1)).findAllBottles();
        verifyNoMoreInteractions(shelfService);
    }

    @Test
    public void findAll_ShouldReturnAllBottleEntriesWithIngredientIds() throws Exception {

        final BottleDto first = new BottleDto();
        first.setId(TEST_ID_1);
        final BeverageDto beverage42 = new BeverageDto();
        beverage42.setId(42);
        first.setBeverage(beverage42);

        final BottleDto second = new BottleDto();
        second.setId(TEST_ID_2);
        final BeverageDto beverage17 = new BeverageDto();
        beverage17.setId(17);
        second.setBeverage(beverage17);

        when(shelfService.findAllBottles()).thenReturn(Arrays.<IBottle>asList(first, second));

        mockMvc.perform(get("/shelf/bottles").accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id", is(TEST_ID_1)))
                .andExpect(jsonPath("$[0].ingredient.id", is(42)))

                .andExpect(jsonPath("$[1].id", is(TEST_ID_2)))
                .andExpect(jsonPath("$[1].ingredient.id", is(17)));

        verify(shelfService, times(1)).findAllBottles();
        verifyNoMoreInteractions(shelfService);
    }

    @Test
    public void create_ShouldCreateNewBottle() throws Exception {

        final BottleBean testBottle = new BottleBean();

        String requestJson = toRequestJson(testBottle);

        when(shelfService.saveBottle(Matchers.any(IBottle.class))).thenReturn(new BottleDto());

        mockMvc.perform(post("/shelf/bottles", requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept("application/json"))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        verify(shelfService, times(1)).saveBottle(Matchers.any(IBottle.class));
        verifyNoMoreInteractions(shelfService);
    }

    @Test
    public void update_ShouldUpdateBottle() throws Exception {
        final BottleDto bottleDto = new BottleDto();
        bottleDto.setId(TEST_ID_1);
        BeverageDto beverageBean = new BeverageDto();
        beverageBean.setId(42);
        beverageBean.setKind("Whiskey");
        beverageBean.setBeverageType(BeverageType.DISTILLED);

        bottleDto.setBeverage(beverageBean);
        bottleDto.setBrandName(BRAND_NAME_1);
        bottleDto.setPrice(PRICE_1);
        bottleDto.setInShelf(true);
        bottleDto.setVolume(VOLUME_1);
        bottleDto.setImageUrl(IMAGE_URL_1);

        when(shelfService.updateBottle(Matchers.any(IBottle.class))).thenReturn(bottleDto);

        String requestJson = toRequestJson(BottleBean.from(bottleDto));

        mockMvc.perform(put("/shelf/bottles/" + TEST_ID_1, requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.id", is(TEST_ID_1)))
                .andExpect(jsonPath("$.ingredient.id", is(42)))
                .andExpect(jsonPath("$.ingredient.kind", is("Whiskey")))
                .andExpect(jsonPath("$.brandName", is(BRAND_NAME_1)))
                .andExpect(jsonPath("$.price", is(PRICE_1)))
                .andExpect(jsonPath("$.inShelf", is(true)))
                .andExpect(jsonPath("$.volume", is(VOLUME_1)))
                .andExpect(jsonPath("$.imageUrl", is(IMAGE_URL_1)));

        verify(shelfService, times(1)).updateBottle(Matchers.any(IBottle.class));
        verifyNoMoreInteractions(shelfService);
    }

    @Test
    public void update_shouldThrowNotFound() throws Exception {
        final BottleBean testBottle = new BottleBean();

        when(shelfService.updateBottle(Matchers.any(IBottle.class))).thenThrow(new BottleNotFoundException(0));

        String requestJson = toRequestJson(testBottle);

        mockMvc.perform(put("/shelf/bottles/" + TEST_ID_1, requestJson).contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        verify(shelfService, times(1)).updateBottle(Matchers.any(IBottle.class));
        verifyNoMoreInteractions(shelfService);
    }

    @Test
    public void delete_ShouldDeleteBottle() throws Exception {

        doNothing().when(shelfService).deleteBottleById(TEST_ID_1);

        mockMvc.perform(delete("/shelf/bottles/" + TEST_ID_1).accept("application/json"))

                .andExpect(status().isNoContent());

        verify(shelfService, times(1)).deleteBottleById(TEST_ID_1);
        verifyNoMoreInteractions(shelfService);
    }

    @Test
    public void delete_shouldThrowNotFound() throws Exception {

        doThrow(new BottleNotFoundException(TEST_ID_2)).when(shelfService).deleteBottleById(TEST_ID_2);

        mockMvc.perform(delete("/shelf/bottles/" + TEST_ID_2).accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        verify(shelfService, times(1)).deleteBottleById(TEST_ID_2);
        verifyNoMoreInteractions(shelfService);
    }

    private String toRequestJson(BottleBean testBottle) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(testBottle);
    }

}