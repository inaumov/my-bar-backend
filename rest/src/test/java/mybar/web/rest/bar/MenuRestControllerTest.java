package mybar.web.rest.bar;

import com.google.common.collect.Lists;
import mybar.dto.bar.MenuDto;
import mybar.service.bar.CocktailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
public class MenuRestControllerTest extends ARestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CocktailsService cocktailsServiceMock;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() throws Exception {
        reset(cocktailsServiceMock);
    }

    @Test
    public void listAllMenuItems_noAuthRequired_And_Should_ReturnAllMenuEntries() throws Exception {

        final MenuDto first = new MenuDto();
        first.setId(1);
        first.setName("shot");

        final MenuDto second = new MenuDto();
        second.setId(2);
        second.setName("long");

        when(cocktailsServiceMock.getAllMenuItems()).thenReturn(Lists.newArrayList(first, second));

        mockMvc.perform(get("/menu"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))

                .andExpect(jsonPath("$[0].name", is("shot")))
                .andExpect(jsonPath("$[0].translation", equalTo("Test Shot")))
                .andExpect(jsonPath("$[1].name", is("long")))
                .andExpect(jsonPath("$[1].translation", equalTo("Test Long")));

        verify(cocktailsServiceMock, times(1)).getAllMenuItems();
        verifyNoMoreInteractions(cocktailsServiceMock);
    }

}