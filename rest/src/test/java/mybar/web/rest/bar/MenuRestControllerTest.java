package mybar.web.rest.bar;

import com.google.common.collect.Lists;
import mybar.api.bar.IMenu;
import mybar.dto.bar.MenuDto;
import mybar.service.bar.CocktailsService;
import mybar.web.rest.TestUtil;
import org.junit.After;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-rest-context.xml")
public class MenuRestControllerTest {

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
        first.setId(1);
        first.setName("shot");

        final MenuDto second = new MenuDto();
        second.setId(2);
        second.setName("long");

        when(cocktailsService.getAllMenuItems()).thenReturn(Lists.<IMenu>newArrayList(first, second));

        mockMvc.perform(get("/menu"))
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

}