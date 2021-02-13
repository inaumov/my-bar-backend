package mybar.web.rest.bar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import mybar.api.bar.ingredient.BeverageType;
import mybar.api.bar.IBottle;
import mybar.app.RestBeanConverter;
import mybar.app.bean.bar.BottleBean;
import mybar.app.bean.bar.ingredient.BeverageBean;
import mybar.dto.bar.BottleDto;
import mybar.dto.bar.ingredient.BeverageDto;
import mybar.exception.BottleNotFoundException;
import mybar.exception.UnknownBeverageException;
import mybar.service.bar.ShelfService;
import mybar.web.rest.ARestControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShelfController.class)
public class ShelfRestControllerTest extends ARestControllerTest {

    public static final String TEST_ID_1 = "bottle-000001";
    public static final String BRAND_NAME_1 = "brand name 1";
    public static final double PRICE_1 = 100;
    public static final double VOLUME_1 = 1;
    public static final String IMAGE_URL_1 = "http://bottle-image1.jpg";

    public static final String TEST_ID_2 = "bottle-000002";
    public static final String BRAND_NAME_2 = "brand name 2";
    public static final double PRICE_2 = 200;
    public static final double VOLUME_2 = 0.5;
    public static final String IMAGE_URL_2 = "http://bottle-image2.jpg";

    public static final String TEST_ID_3 = "bottle-000067";
    public static final double PRICE_3 = 2176.99;
    public static final double VOLUME_3 = 2;
    public static final String IMAGE_URL_3 = "http://martell.jpg";
    public static final int INGREDIENT_ID_3 = 42;
    public static final String KIND_3 = "Cognac";
    public static final String BRAND_NAME_3 = "Martell";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShelfService shelfServiceMock;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() throws Exception {
        reset(shelfServiceMock);
    }

    @Test
    public void test_findAll_notAuthorized() throws Exception {
        String accessToken = "not_a_real_token";

        when(shelfServiceMock.findById(Mockito.anyString())).thenReturn(Mockito.mock(IBottle.class));

        mockMvc.perform(get("/ingredients")
                .header("Authorization", "Bearer " + accessToken)
                .accept(CONTENT_TYPE))

                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void test_findAll_wrongRole() throws Exception {

        when(shelfServiceMock.findById(Mockito.anyString())).thenReturn(Mockito.mock(IBottle.class));

        mockMvc.perform(makePreAuthorizedRequest(ADMIN, ADMIN, get("/ingredients")))

                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(status().isForbidden());
    }

    @Test
    public void findById_Should_ReturnBottleEntry() throws Exception {

        final BottleDto first = new BottleDto();
        first.setId(TEST_ID_1);
        first.setBrandName(BRAND_NAME_1);
        first.setPrice(BigDecimal.valueOf(PRICE_1));
        first.setInShelf(true);
        first.setVolume(VOLUME_1);
        first.setImageUrl(IMAGE_URL_1);
        first.setBeverage(new BeverageDto());

        when(shelfServiceMock.findById(TEST_ID_1)).thenReturn(first);

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, get("/shelf/bottles/{0}", TEST_ID_1)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$.id", is(TEST_ID_1)))
                .andExpect(jsonPath("$.brandName", is(BRAND_NAME_1)))
                .andExpect(jsonPath("$.price", comparesEqualTo(PRICE_1)))
                .andExpect(jsonPath("$.inShelf", is("YES")))
                .andExpect(jsonPath("$.volume", is(VOLUME_1)))
                .andExpect(jsonPath("$.imageUrl", is(IMAGE_URL_1)));

        verify(shelfServiceMock, times(1)).findById(anyString());
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void findById_Should_ThrowNotFound() throws Exception {
        when(shelfServiceMock.findById(TEST_ID_2)).thenThrow(new BottleNotFoundException(TEST_ID_2));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, get("/shelf/bottles/{0}", TEST_ID_2)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"There is no bottle with id: " + TEST_ID_2)));

        verify(shelfServiceMock, times(1)).findById(anyString());
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void findAll_Should_ReturnEmptyArray() throws Exception {

        when(shelfServiceMock.findAllBottles()).thenReturn(Collections.<IBottle>emptyList());

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, get("/shelf/bottles")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$", hasSize(0)));

        verify(shelfServiceMock, times(1)).findAllBottles();
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void findAll_Should_ReturnAllBottleEntriesWithoutIngredients() throws Exception {

        final BottleDto first = new BottleDto();
        first.setId(TEST_ID_1);
        first.setBrandName(BRAND_NAME_1);
        first.setPrice(BigDecimal.valueOf(PRICE_1));
        first.setInShelf(true);
        first.setVolume(VOLUME_1);
        first.setImageUrl(IMAGE_URL_1);

        final BottleDto second = new BottleDto();
        second.setId(TEST_ID_2);
        second.setBrandName(BRAND_NAME_2);
        second.setPrice(BigDecimal.valueOf(PRICE_2));
        second.setInShelf(false);
        second.setVolume(VOLUME_2);
        second.setImageUrl(IMAGE_URL_2);

        when(shelfServiceMock.findAllBottles()).thenReturn(Arrays.<IBottle>asList(first, second));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, get("/shelf/bottles")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id", is(TEST_ID_1)))
                .andExpect(jsonPath("$[0].brandName", is(BRAND_NAME_1)))
                .andExpect(jsonPath("$[0].price", comparesEqualTo(PRICE_1)))
                .andExpect(jsonPath("$[0].inShelf", is("YES")))
                .andExpect(jsonPath("$[0].volume", is(VOLUME_1)))
                .andExpect(jsonPath("$[0].imageUrl", is(IMAGE_URL_1)))

                .andExpect(jsonPath("$[1].id", is(TEST_ID_2)))
                .andExpect(jsonPath("$[1].brandName", is(BRAND_NAME_2)))
                .andExpect(jsonPath("$[1].price", comparesEqualTo(PRICE_2)))
                .andExpect(jsonPath("$[1].inShelf", is("NO")))
                .andExpect(jsonPath("$[1].volume", is(VOLUME_2)))
                .andExpect(jsonPath("$[1].imageUrl", is(IMAGE_URL_2)));

        verify(shelfServiceMock, times(1)).findAllBottles();
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void findAll_Should_ReturnAllBottleEntriesWithIngredientIds() throws Exception {

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

        when(shelfServiceMock.findAllBottles()).thenReturn(Arrays.<IBottle>asList(first, second));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, get("/shelf/bottles")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id", is(TEST_ID_1)))
                .andExpect(jsonPath("$[0].ingredient.id", is(42)))

                .andExpect(jsonPath("$[1].id", is(TEST_ID_2)))
                .andExpect(jsonPath("$[1].ingredient.id", is(17)));

        verify(shelfServiceMock, times(1)).findAllBottles();
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void create_Should_CreateNewBottle() throws Exception {

        BottleDto prepareBottleDto = prepareBottleDto();
        String requestJson = toRequestJson(RestBeanConverter.from(prepareBottleDto));

        when(shelfServiceMock.saveBottle(Mockito.any(IBottle.class))).thenReturn(prepareBottleDto);

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, post("/shelf/bottles", requestJson))
                .content(requestJson))

                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$.id", is(TEST_ID_3)))
                .andExpect(jsonPath("$.ingredient.id", is(INGREDIENT_ID_3)))
                .andExpect(jsonPath("$.ingredient.kind", is(KIND_3)))
                .andExpect(jsonPath("$.brandName", is(BRAND_NAME_3)))
                .andExpect(jsonPath("$.price", comparesEqualTo(PRICE_3)))
                .andExpect(jsonPath("$.inShelf", is("YES")))
                .andExpect(jsonPath("$.volume", is(VOLUME_3)))
                .andExpect(jsonPath("$.imageUrl", is(IMAGE_URL_3)));

        verify(shelfServiceMock, times(1)).saveBottle(Mockito.any(IBottle.class));
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void create_Should_ThrowIngredientUnknown() throws Exception {

        BeverageBean beverage = new BeverageBean();
        beverage.setId(102);
        when(shelfServiceMock.saveBottle(Mockito.any(IBottle.class))).thenThrow(new UnknownBeverageException(beverage));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, post("/shelf/bottles"))
                .content("{}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Beverage [102] is unknown.")));

        verify(shelfServiceMock, times(1)).saveBottle(Mockito.any(IBottle.class));
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void create_Should_ValidateBrandNameRequired() throws Exception {

        when(shelfServiceMock.saveBottle(Mockito.any(IBottle.class))).thenCallRealMethod();

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, post("/shelf/bottles"))
                .content("{\"ingredient\":{\"id\":6}}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Brand name is required.")));

        verify(shelfServiceMock, times(1)).saveBottle(Mockito.any(IBottle.class));
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void create_Should_ValidateBeverageIdRequired() throws Exception {

        when(shelfServiceMock.saveBottle(Mockito.any(IBottle.class))).thenCallRealMethod();

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, post("/shelf/bottles"))
                .content("{\"brandName\":\"test\"}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Beverage ID is required.")));

        verify(shelfServiceMock, times(1)).saveBottle(Mockito.any(IBottle.class));
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void update_Should_UpdateBottle() throws Exception {
        final BottleDto bottleDto = prepareBottleDto();

        when(shelfServiceMock.updateBottle(Mockito.any(IBottle.class))).thenReturn(bottleDto);

        String requestJson = toRequestJson(RestBeanConverter.from(bottleDto));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/shelf/bottles/", requestJson))
                .content(requestJson))

                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$.id", is(TEST_ID_3)))
                .andExpect(jsonPath("$.ingredient.id", is(INGREDIENT_ID_3)))
                .andExpect(jsonPath("$.ingredient.kind", is(KIND_3)))
                .andExpect(jsonPath("$.brandName", is(BRAND_NAME_3)))
                .andExpect(jsonPath("$.price", comparesEqualTo(PRICE_3)))
                .andExpect(jsonPath("$.inShelf", is("YES")))
                .andExpect(jsonPath("$.volume", is(VOLUME_3)))
                .andExpect(jsonPath("$.imageUrl", is(IMAGE_URL_3)));

        verify(shelfServiceMock, times(1)).updateBottle(Mockito.any(IBottle.class));
        verifyNoMoreInteractions(shelfServiceMock);
    }

    private BottleDto prepareBottleDto() {
        final BottleDto bottleDto = new BottleDto();
        bottleDto.setId(TEST_ID_3);
        BeverageDto beverageBean = new BeverageDto();
        beverageBean.setId(INGREDIENT_ID_3);
        beverageBean.setKind(KIND_3);
        beverageBean.setBeverageType(BeverageType.DISTILLED);

        bottleDto.setBeverage(beverageBean);
        bottleDto.setBrandName(BRAND_NAME_3);
        bottleDto.setPrice(BigDecimal.valueOf(PRICE_3));
        bottleDto.setInShelf(true);
        bottleDto.setVolume(VOLUME_3);
        bottleDto.setImageUrl(IMAGE_URL_3);
        return bottleDto;
    }

    @Test
    public void update_Should_ThrowNotFound() throws Exception {
        final BottleBean testBottle = new BottleBean();

        when(shelfServiceMock.updateBottle(Mockito.any(IBottle.class))).thenThrow(new BottleNotFoundException(TEST_ID_2));

        String requestJson = toRequestJson(testBottle);

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/shelf/bottles/", requestJson))
                .content(requestJson))

                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"There is no bottle with id: " + TEST_ID_2)));

        verify(shelfServiceMock, times(1)).updateBottle(Mockito.any(IBottle.class));
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void update_Should_ThrowIngredientUnknown() throws Exception {

        BeverageBean beverageBean = new BeverageBean();
        beverageBean.setId(21);
        when(shelfServiceMock.updateBottle(Mockito.any(IBottle.class))).thenThrow(new UnknownBeverageException(beverageBean));

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/shelf/bottles"))
                .content("{}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Beverage [21] is unknown.")));

        verify(shelfServiceMock, times(1)).updateBottle(Mockito.any(IBottle.class));
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void update_Should_ValidateBrandNameRequired() throws Exception {

        when(shelfServiceMock.updateBottle(Mockito.any(IBottle.class))).thenCallRealMethod();

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/shelf/bottles"))
                .content("{\"id\":\"bottle-test00\",\"ingredient\":{\"id\":21}}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Brand name is required")));

        verify(shelfServiceMock, times(1)).updateBottle(Mockito.any(IBottle.class));
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void update_Should_ValidateBeverageIdRequired() throws Exception {

        when(shelfServiceMock.updateBottle(Mockito.any(IBottle.class))).thenCallRealMethod();

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, put("/shelf/bottles"))
                .content("{\"id\":\"bottle-test00\",\"brandName\":\"test\"}"))

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"Beverage ID is required.")));

        verify(shelfServiceMock, times(1)).updateBottle(Mockito.any(IBottle.class));
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void delete_Should_DeleteBottle() throws Exception {

        doNothing().when(shelfServiceMock).deleteBottleById(TEST_ID_1);

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, delete("/shelf/bottles/" + TEST_ID_1)))

                .andDo(print())
                .andExpect(status().isNoContent());

        verify(shelfServiceMock, times(1)).deleteBottleById(TEST_ID_1);
        verifyNoMoreInteractions(shelfServiceMock);
    }

    @Test
    public void delete_Should_ThrowNotFound() throws Exception {

        doThrow(new BottleNotFoundException(TEST_ID_2)).when(shelfServiceMock).deleteBottleById(TEST_ID_2);

        mockMvc.perform(makePreAuthorizedRequest(USER, USER, delete("/shelf/bottles/" + TEST_ID_2)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"There is no bottle with id: " + TEST_ID_2)));

        verify(shelfServiceMock, times(1)).deleteBottleById(TEST_ID_2);
        verifyNoMoreInteractions(shelfServiceMock);
    }

    private String toRequestJson(BottleBean testBottle) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(testBottle);
    }

}