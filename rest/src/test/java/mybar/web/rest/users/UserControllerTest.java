package mybar.web.rest.users;

import mybar.api.users.IUser;
import mybar.api.users.RoleName;
import mybar.dto.users.UserDto;
import mybar.exception.users.EmailDuplicatedException;
import mybar.exception.users.UnknownUserException;
import mybar.exception.users.UserExistsException;
import mybar.service.users.UserService;
import mybar.web.rest.ARestControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
public class UserControllerTest extends ARestControllerTest {

    public static final String USERNAME = "joe";
    public static final String USERNAME_OBFUSCATED = "jo***";
    public static final String NAME = "Joe";
    public static final String SURNAME = "Keery";
    public static final String EMAIL = "joe@example.com";
    public static final String EMAIL_OBFUSCATED = "j***@e***e.com";
    public static final String PASSWORD = "am9lLnB3ZA==";

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
    }

    @AfterEach
    public void tearDown() {
        reset(userService);
    }

    @Test
    public void test_register_new_user_anonymously() throws Exception {
        UserDto userDto = prepareUserDto();
        when(userService.createUser(Mockito.any(IUser.class))).thenReturn(userDto);

        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/users/register")
                        .contentType(CONTENT_TYPE)
                        .content(createUserInJson(
                                USERNAME,
                                EMAIL,
                                PASSWORD)
                        );

        this.mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())

                .andExpect(jsonPath("$.username", is(USERNAME)))
                .andExpect(jsonPath("$.email", is(EMAIL)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.surname", is(SURNAME)))

                .andExpect(jsonPath("$.active").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.roles").doesNotExist());

        verify(userService, atLeastOnce()).createUser(Mockito.any(IUser.class));
    }

    private static String createUserInJson(String name, String email, String password) {
        return "{" +
                "\"username\":\"" + name + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"passwordConfirm\":\"" + password + "\"" +
                "}";
    }

    @Test
    public void test_register_new_user_when_email_duplicated() throws Exception {
        when(userService.createUser(Mockito.any(IUser.class))).thenThrow(new EmailDuplicatedException(EMAIL));

        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserInJson(
                                USERNAME,
                                EMAIL,
                                PASSWORD)
                        );

        this.mockMvc.perform(requestBuilder)

                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(MockMvcResultMatchers.status()
                        .isForbidden())

                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("errorMessage\":\"There is an account with that email: " + EMAIL)));

        verify(userService, atLeastOnce()).createUser(Mockito.any(IUser.class));
    }

    @Test
    public void test_register_new_user_when_already_exists() throws Exception {
        when(userService.createUser(Mockito.any(IUser.class))).thenThrow(new UserExistsException(USERNAME));

        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserInJson(
                                USERNAME,
                                EMAIL,
                                PASSWORD)
                        );

        this.mockMvc.perform(requestBuilder)

                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(MockMvcResultMatchers.status()
                        .isForbidden())

                .andExpect(content().string(containsString("errorMessage\":\"Username [" + USERNAME + "] already occupied.")));

        verify(userService, atLeastOnce()).createUser(Mockito.any(IUser.class));
    }

    @Test
    public void test_get_all_users_authenticated_with_role_admin() throws Exception {
        UserDto userDto = prepareUserDto();
        when(userService.getAllRegisteredUsers()).thenReturn(Collections.singletonList(userDto));

        // get all users
        MockHttpServletRequestBuilder requestBuilder = makePreAuthorizedRequest(ADMIN, ADMIN, MockMvcRequestBuilders.get("/users"));

        this.mockMvc.perform(requestBuilder)

                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())

                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.users", hasSize(1)))

                .andExpect(jsonPath("$.users[0].username", is(USERNAME_OBFUSCATED)))
                .andExpect(jsonPath("$.users[0].email", is(EMAIL_OBFUSCATED)))
                .andExpect(jsonPath("$.users[0].name", is(NAME)))
                .andExpect(jsonPath("$.users[0].surname", is(SURNAME)))
                .andExpect(jsonPath("$.users[0].active", is(true)))
                .andExpect(jsonPath("$.users[0].roles").isArray())
                .andExpect(jsonPath("$.users[0].roles", Matchers.contains("ROLE_USER")))

                .andExpect(jsonPath("$.users[0].password").doesNotExist());

        verify(userService, atLeastOnce()).getAllRegisteredUsers();
    }

    @Test
    public void test_get_all_users_unauthorized_with_role_user() throws Exception {
        UserDto userDto = prepareUserDto();
        when(userService.getAllRegisteredUsers()).thenReturn(Collections.singletonList(userDto));

        // get all users
        MockHttpServletRequestBuilder requestBuilder = makePreAuthorizedRequest(USER, USER, MockMvcRequestBuilders.get("/users"));

        this.mockMvc.perform(requestBuilder)

                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(MockMvcResultMatchers.status()
                        .isForbidden());
    }

    @Test
    public void test_update_user_info_authenticated_with_role_user() throws Exception {
        UserDto userDto = prepareUserDto();
        userDto.setEmail("joe@gmail.com");
        when(userService.editUserInfo(Mockito.any(IUser.class))).thenReturn(userDto);

        MockHttpServletRequestBuilder requestBuilder = makePreAuthorizedRequest(USER, USER, MockMvcRequestBuilders.put("/users"))
                .content(createUserInJson(
                        USERNAME,
                        "joe@gmail.com",
                        PASSWORD)
                );

        this.mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())

                .andExpect(jsonPath("$.username", is(USERNAME)))
                .andExpect(jsonPath("$.email", is("joe@gmail.com")))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.surname", is(SURNAME)))

                .andExpect(jsonPath("$.active").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.roles").doesNotExist());

        verify(userService, atLeastOnce()).editUserInfo(Mockito.any(IUser.class));
    }

    @Test
    public void test_deactivate_user_authenticated_with_role_user() throws Exception {
        doNothing().when(userService).deactivateUser(eq(USERNAME));

        MockHttpServletRequestBuilder requestBuilder = makePreAuthorizedRequest(USER, USER, MockMvcRequestBuilders.delete("/users/" + USERNAME));

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isNoContent())
                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).deactivateUser(eq(USERNAME));
    }

    @Test
    public void test_find_by_username_authenticated_with_role_user() throws Exception {
        UserDto userDto = prepareUserDto();
        when(userService.findByUsername(eq(USERNAME))).thenReturn(userDto);

        MockHttpServletRequestBuilder requestBuilder = makePreAuthorizedRequest(USER, USER, MockMvcRequestBuilders.get("/users/" + USERNAME));

        this.mockMvc.perform(requestBuilder)

                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())

                .andExpect(jsonPath("$.username", is(USERNAME)))
                .andExpect(jsonPath("$.email", is(EMAIL)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.surname", is(SURNAME)))

                .andExpect(jsonPath("$.active").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.roles").doesNotExist());

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

        MockHttpServletRequestBuilder requestBuilder = makePreAuthorizedRequest(USER, USER, MockMvcRequestBuilders.get("/users/" + USERNAME));

        this.mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())

                .andExpect(content().string(containsString("errorMessage\":\"User=[" + USERNAME + "] is unknown.")))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).findByUsername(USERNAME);
    }

}