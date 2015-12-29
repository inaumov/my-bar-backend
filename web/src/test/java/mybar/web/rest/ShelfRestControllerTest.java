package mybar.web.rest;

import mybar.api.bar.IBottle;
import mybar.service.bar.ShelfService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
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

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
//@WebAppConfiguration
public class ShelfRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShelfService shelfServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    //@Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    //@Test
    public void findAll_ShouldReturnAllBottleEntries() throws Exception {

        IBottle first = mock(IBottle.class);
        when(first.getId()).thenReturn(TEST_ID);
        when(first.getBrandName()).thenReturn(BRAND_NAME);
        when(first.getPrice()).thenReturn(PRICE);

        IBottle second = mock(IBottle.class);
        when(second.getId()).thenReturn(TEST_ID);
        when(second.getBrandName()).thenReturn(BRAND_NAME);
        when(second.getPrice()).thenReturn(PRICE);

        when(shelfServiceMock.findAllBottles()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/shelf/bottles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].brandName", is(BRAND_NAME)))
                .andExpect(jsonPath("$[0].price", is(PRICE)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].brandName", is(BRAND_NAME)))
                .andExpect(jsonPath("$[1].price", is(PRICE)));

        verify(shelfServiceMock, times(1)).findAllBottles();
        verifyNoMoreInteractions(shelfServiceMock);
    }

}