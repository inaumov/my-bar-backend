package mybar.web.rest.bar;

import mybar.api.bar.IBottle;
import mybar.dto.bar.BottleDto;
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

import javax.servlet.Filter;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"test-rest-context.xml", "test-security-context.xml"})
public class ShelfRestControllerAuthTest {
    public static final String USERNAME = "joe";
    public static final String ROLE_USER = "USER";

    public static final String TEST_ID = "bottle-000001";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ShelfService shelfService;

    @Before
    public void setUp() {
        Filter springSecurityFilterChain = (Filter) webApplicationContext.getBean("springSecurityFilterChain");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        reset(shelfService);
    }

    @Test
    public void test_findById() throws Exception {
        when(shelfService.findById(TEST_ID)).thenReturn(new BottleDto());

        mockMvc.perform(get("/shelf/bottles/" + TEST_ID)
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123")))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    public void test_findAll() throws Exception {

        when(shelfService.findAllBottles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/shelf/bottles")
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123")))

                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void test_create() throws Exception {
        when(shelfService.saveBottle(Matchers.any(IBottle.class))).thenReturn(new BottleDto());

        mockMvc.perform(post("/shelf/bottles")
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123"))

                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isCreated());
    }

    @Test
    public void test_update() throws Exception {
        when(shelfService.updateBottle(Matchers.any(IBottle.class))).thenReturn(new BottleDto());

        mockMvc.perform(put("/shelf/bottles/")
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_delete() throws Exception {
        doNothing().when(shelfService).deleteBottleById(TEST_ID);

        mockMvc.perform(delete("/shelf/bottles/" + TEST_ID)
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123")))

                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(status().isNoContent());
    }

}