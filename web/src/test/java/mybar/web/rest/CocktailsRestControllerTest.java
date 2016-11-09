package mybar.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IMenu;
import mybar.app.bean.bar.CocktailBean;
import mybar.dto.bar.CocktailDto;
import mybar.dto.bar.MenuDto;
import mybar.exception.CocktailNotFoundException;
import mybar.service.bar.CocktailsService;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-rest-context.xml")
public class CocktailsRestControllerTest {

    public static final int TEST_ID_1 = 1;
    public static final int TEST_ID_2 = 2;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private CocktailsService cocktailsService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
        reset(cocktailsService);
    }

    @Test
    public void listAllMenuItems_Should_ReturnAllMenuEntries() throws Exception {

        final MenuDto first = new MenuDto();
        first.setId(TEST_ID_1);
        first.setName("shot");

        final MenuDto second = new MenuDto();
        second.setId(TEST_ID_2);
        second.setName("long");

        when(cocktailsService.getAllMenuItems()).thenReturn(Lists.<IMenu>newArrayList(first, second));

        mockMvc.perform(get("/cocktails/menu"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$[0].name", is("shot")))
                .andExpect(jsonPath("$[0].translation", equalTo("Test Shot")))
                .andExpect(jsonPath("$[1].name", is("long")))
                .andExpect(jsonPath("$[1].translation", equalTo("Test Long")));

        verify(cocktailsService, times(1)).getAllMenuItems();
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void findById_Should_ReturnCocktailEntry() throws Exception {

        final CocktailDto first = new CocktailDto();
        first.setId(TEST_ID_1);

        when(cocktailsService.findCocktailById(TEST_ID_1)).thenReturn(first);

        mockMvc.perform(get("/cocktails/" + TEST_ID_1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.id", is(TEST_ID_1)));

        verify(cocktailsService, times(1)).findCocktailById(anyInt());
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void findById_Should_ThrowNotFound() throws Exception {
        when(cocktailsService.findCocktailById(TEST_ID_2)).thenThrow(new CocktailNotFoundException(TEST_ID_2));

        mockMvc.perform(get("/cocktails/" + TEST_ID_2))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"There is no cocktail with id: " + TEST_ID_2)));

        verify(cocktailsService, times(1)).findCocktailById(anyInt());
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void findAll_Should_ReturnEmptyArray() throws Exception {

        when(cocktailsService.getAllCocktails()).thenReturn(Collections.<String, List<ICocktail>>emptyMap());

        mockMvc.perform(get("/cocktails"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.*", hasSize(0)));

        verify(cocktailsService, times(1)).getAllCocktails();
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void findAll_Should_ReturnAllCocktailEntries() throws Exception {

        final CocktailDto first = new CocktailDto();
        first.setId(TEST_ID_1);

        final CocktailDto second = new CocktailDto();
        second.setId(TEST_ID_2);

        ImmutableMap<String, List<ICocktail>> cocktails = ImmutableMap.<String, List<ICocktail>>of(
                "long", Lists.<ICocktail>newArrayList(first),
                "shot", Lists.<ICocktail>newArrayList(second)
        );
        when(cocktailsService.getAllCocktails()).thenReturn(cocktails);

        mockMvc.perform(get("/cocktails"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.long.", hasSize(1)))
                .andExpect(jsonPath("$.long[0].id", is(TEST_ID_1)))

                .andExpect(jsonPath("$.shot.", hasSize(1)))
                .andExpect(jsonPath("$.shot[0].id", is(TEST_ID_2)));

        verify(cocktailsService, times(1)).getAllCocktails();
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void create_Should_CreateNewCocktail() throws Exception {

        final CocktailBean testCocktail = new CocktailBean();
        String requestJson = toRequestJson(testCocktail);

        when(cocktailsService.saveOrUpdateCocktail(Matchers.any(ICocktail.class))).thenReturn(new CocktailDto());

        mockMvc.perform(post("/cocktails", requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept("application/json"))

                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        verify(cocktailsService, times(1)).saveOrUpdateCocktail(Matchers.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void update_Should_UpdateCocktail() throws Exception {
        final CocktailDto bottleDto = new CocktailDto();
        bottleDto.setId(TEST_ID_1);

        when(cocktailsService.saveOrUpdateCocktail(Matchers.any(ICocktail.class))).thenReturn(bottleDto);

        String requestJson = toRequestJson(CocktailBean.from(bottleDto));

        mockMvc.perform(put("/cocktails/", requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept("application/json"))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.id", is(TEST_ID_1)));


        verify(cocktailsService, times(1)).saveOrUpdateCocktail(Matchers.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void update_Should_ThrowNotFound() throws Exception {
        final CocktailBean testCocktail = new CocktailBean();

        when(cocktailsService.saveOrUpdateCocktail(Matchers.any(ICocktail.class))).thenThrow(new CocktailNotFoundException(TEST_ID_2));

        String requestJson = toRequestJson(testCocktail);

        mockMvc.perform(put("/cocktails/", requestJson).contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept("application/json"))

                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"There is no cocktail with id: " + TEST_ID_2)));

        verify(cocktailsService, times(1)).saveOrUpdateCocktail(Matchers.any(ICocktail.class));
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void delete_Should_DeleteCocktail() throws Exception {

        doNothing().when(cocktailsService).deleteCocktailById(TEST_ID_1);

        mockMvc.perform(delete("/cocktails/" + TEST_ID_1).accept("application/json"))

                .andDo(print())
                .andExpect(status().isNoContent());

        verify(cocktailsService, times(1)).deleteCocktailById(TEST_ID_1);
        verifyNoMoreInteractions(cocktailsService);
    }

    @Test
    public void delete_Should_ThrowNotFound() throws Exception {

        doThrow(new CocktailNotFoundException(TEST_ID_2)).when(cocktailsService).deleteCocktailById(TEST_ID_2);

        mockMvc.perform(delete("/cocktails/" + TEST_ID_2).accept("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"There is no cocktail with id: " + TEST_ID_2)));

        verify(cocktailsService, times(1)).deleteCocktailById(TEST_ID_2);
        verifyNoMoreInteractions(cocktailsService);
    }

    private String toRequestJson(CocktailBean testCocktail) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(testCocktail);
    }

}