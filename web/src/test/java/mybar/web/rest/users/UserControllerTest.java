package mybar.web.rest.users;

import mybar.api.users.IUser;
import mybar.api.users.RoleName;
import mybar.dto.users.UserDto;
import mybar.exception.users.EmailDuplicatedException;
import mybar.exception.users.UnknownUserException;
import mybar.exception.users.UserExistsException;
import mybar.service.users.UserService;
import mybar.web.rest.TestUtil;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"test-users-rest-context.xml", "test-security-context.xml"})
public class UserControllerTest {

    public static final String USERNAME = "joe";
    public static final String USERNAME_OBFUSCATED = "jo***";
    public static final String NAME = "Joe";
    public static final String SURNAME = "Keery";
    public static final String EMAIL = "joe@example.com";
    public static final String EMAIL_OBFUSCATED = "j***@e***e.com";
    public static final String PASSWORD = "joe.pwd";

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Before
    public void setup() {
        Filter springSecurityFilterChain = (Filter) webApplicationContext.getBean("springSecurityFilterChain");

        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.webApplicationContext);

        this.mockMvc = builder
                .addFilters(springSecurityFilterChain)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        reset(userService);
    }

    @Test
    public void test_register_new_user_anonymously() throws Exception {
        UserDto userDto = prepareUserDto();
        when(userService.createUser(any(IUser.class))).thenReturn(userDto);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserInJson(
                                USERNAME,
                                EMAIL,
                                PASSWORD)
                        );

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.username", is(USERNAME)))
                .andExpect(jsonPath("$.email", is(EMAIL)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.surname", is(SURNAME)))

                .andExpect(jsonPath("$.active").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.roles").doesNotExist())

                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).createUser(any(IUser.class));
    }

    private static String createUserInJson(String name, String email, String password) {
        return "{" +
                "\"username\": \"" + name + "\", " +
                "\"name\": \"" + name + "\", " +
                "\"emailAddress\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"" +
                "}";
    }

    @Test
    public void test_register_new_user_when_email_duplicated() throws Exception {
        when(userService.createUser(any(IUser.class))).thenThrow(new EmailDuplicatedException(EMAIL));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserInJson(
                                USERNAME,
                                EMAIL,
                                PASSWORD)
                        );

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isForbidden())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"There is an account with that email: " + EMAIL)))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).createUser(any(IUser.class));
    }

    @Test
    public void test_register_new_user_when_already_exists() throws Exception {
        when(userService.createUser(any(IUser.class))).thenThrow(new UserExistsException(USERNAME));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserInJson(
                                USERNAME,
                                EMAIL,
                                PASSWORD)
                        );

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isForbidden())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"Username [" + USERNAME + "] already occupied.")))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).createUser(any(IUser.class));
    }

    @Test
    public void test_get_all_users_authenticated_with_role_admin() throws Exception {
        UserDto userDto = prepareUserDto();
        when(userService.getAllRegisteredUsers()).thenReturn(Collections.singletonList(userDto));

        // get all users
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users")
                .with(user(USERNAME).password("abc123").roles(ROLE_ADMIN))
                .with(httpBasic(USERNAME, "abc123"))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder)
                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.users", hasSize(1)))

                .andExpect(jsonPath("$.users[0].username", is(USERNAME_OBFUSCATED)))
                .andExpect(jsonPath("$.users[0].email", is(EMAIL_OBFUSCATED)))
                .andExpect(jsonPath("$.users[0].name", is(NAME)))
                .andExpect(jsonPath("$.users[0].surname", is(SURNAME)))
                .andExpect(jsonPath("$.users[0].active", is(true)))
                .andExpect(jsonPath("$.users[0].roles").isArray())
                .andExpect(jsonPath("$.users[0].roles", Matchers.contains("ROLE_USER")))

                .andExpect(jsonPath("$.users[0].password").doesNotExist())

                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).getAllRegisteredUsers();
    }

    @Test
    public void test_update_user_info_authenticated_with_role_user() throws Exception {
        UserDto userDto = prepareUserDto();
        userDto.setEmail("joe@gmail.com");
        when(userService.editUserInfo(any(IUser.class))).thenReturn(userDto);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/users")
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserInJson(
                        USERNAME,
                        "joe@gmail.com",
                        PASSWORD)
                )
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder)
                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.username", is(USERNAME)))
                .andExpect(jsonPath("$.email", is("joe@gmail.com")))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.surname", is(SURNAME)))

                .andExpect(jsonPath("$.active").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.roles").doesNotExist())

                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).editUserInfo(any(IUser.class));
    }

    @Test
    public void test_deactivate_user_authenticated_with_role_user() throws Exception {
        doNothing().when(userService).deactivateUser(eq(USERNAME));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/users/" + USERNAME)
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123"))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder)
                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(MockMvcResultMatchers.status()
                        .isNoContent())
                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).deactivateUser(eq(USERNAME));
    }

    @Test
    public void test_find_by_username_authenticated_with_role_user() throws Exception {
        UserDto userDto = prepareUserDto();
        when(userService.findByUsername(eq(USERNAME))).thenReturn(userDto);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users/" + USERNAME)
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123"))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder)
                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                .andExpect(jsonPath("$.username", is(USERNAME)))
                .andExpect(jsonPath("$.email", is(EMAIL)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.surname", is(SURNAME)))

                .andExpect(jsonPath("$.active").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.roles").doesNotExist())

                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).findByUsername(USERNAME);
    }

    private UserDto prepareUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUsername(USERNAME);
        userDto.setActive(true);
        userDto.setName(NAME);
        userDto.setSurname(SURNAME);
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setRoles(Collections.singletonList(RoleName.ROLE_USER.name()));
        return userDto;
    }

    @Test
    public void test_find_by_username_when_unknown() throws Exception {

        when(userService.findByUsername(eq(USERNAME))).thenThrow(new UnknownUserException(USERNAME));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users/" + USERNAME)
                .with(user(USERNAME).password("abc123").roles(ROLE_USER))
                .with(httpBasic(USERNAME, "abc123"))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder)
                .andExpect(authenticated().withUsername(USERNAME))
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("errorMessage\":\"User=[" + USERNAME + "] is unknown.")))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).findByUsername(USERNAME);
    }

}