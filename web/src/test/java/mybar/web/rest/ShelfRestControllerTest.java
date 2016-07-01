package mybar.web.rest;

import mybar.BeverageType;
import mybar.api.bar.IBottle;
import mybar.api.bar.ingredient.IBeverage;
import mybar.dto.bar.ingredient.BeverageDto;
import mybar.service.bar.ShelfService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static mybar.dto.bar.BottleTest.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-rest-context.xml")
public class ShelfRestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ShelfService shelfService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void findAll_ShouldReturnAllBottleEntries() throws Exception {

        IBottle first = mock(IBottle.class);
        when(first.getId()).thenReturn(TEST_ID);

        final BeverageDto beverageMock = mock(BeverageDto.class);
        when(beverageMock.getId()).thenReturn(42);
        when(beverageMock.getKind()).thenReturn("Whiskey");
        when(beverageMock.getBeverageType()).thenReturn(BeverageType.DISTILLED);

        when(first.getBeverage()).thenReturn(beverageMock);
        when(first.getBrandName()).thenReturn(BRAND_NAME);
        when(first.getPrice()).thenReturn(PRICE);
        when(first.isInShelf()).thenReturn(true);
        when(first.getVolume()).thenReturn(VOLUME);
        when(first.getImageUrl()).thenReturn(IMAGE_URL);

        IBottle second = mock(IBottle.class);
        when(second.getId()).thenReturn(2);
        when(second.getBrandName()).thenReturn("brand_2");
        when(second.getPrice()).thenReturn(PRICE);

        when(shelfService.findAllBottles()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/shelf/bottles").accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].ingredient.id", is(42)))
                .andExpect(jsonPath("$[0].brandName", is(BRAND_NAME)))
                .andExpect(jsonPath("$[0].price", is(PRICE)))
                .andExpect(jsonPath("$[0].inShelf", is(true)))
                .andExpect(jsonPath("$[0].volume", is(VOLUME)))
                .andExpect(jsonPath("$[0].imageUrl", is(IMAGE_URL)))

                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].brandName", is("brand_2")))
                .andExpect(jsonPath("$[1].price", is(PRICE)));

        verify(shelfService, times(1)).findAllBottles();
        verifyNoMoreInteractions(shelfService);
    }

}