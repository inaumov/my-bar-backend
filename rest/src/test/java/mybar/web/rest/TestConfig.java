package mybar.web.rest;

import mybar.api.users.IUser;
import mybar.service.users.UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {

    private static UserDetails buildActive(User.UserBuilder userBuilder) {
        return userBuilder
                .accountExpired(false)
                .accountLocked(false)
                .disabled(false)
                .credentialsExpired(false)
                .build();
    }

    @Bean
    public UserService userService() {
        IUser userMock = Mockito.mock(IUser.class);
        when(userMock.isActive()).thenReturn(true);
        when(userMock.getUsername()).thenReturn("test");
        when(userMock.getPassword()).thenReturn("user");
        when(userMock.getRoles()).thenReturn(Collections.singletonList("ROLE_ANY"));

        UserService userServiceMock = Mockito.mock(UserService.class);
        Mockito.when(userServiceMock.findByUsername(Mockito.anyString())).thenReturn(userMock);
        return userServiceMock;
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {

        User.UserBuilder testUserBuilder = User
                .withUsername("user")
                .password("user")
                .roles("USER");
        UserDetails testUser = buildActive(testUserBuilder);

        User.UserBuilder adminUserBuilder = User
                .withUsername("admin")
                .password("admin")
                .roles("ADMIN");
        UserDetails adminUser = buildActive(adminUserBuilder);

        User.UserBuilder analystUserBuilder = User
                .withUsername("analyst")
                .password("analyst")
                .roles("ANALYST");
        UserDetails analystUser = buildActive(analystUserBuilder);

        return new InMemoryUserDetailsManager(Arrays.asList(testUser, adminUser, analystUser));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PlainTextPasswordEncoder();
    }

    private static class PlainTextPasswordEncoder implements PasswordEncoder {

        @Override
        public String encode(CharSequence charSequence) {
            return charSequence.toString();
        }

        @Override
        public boolean matches(CharSequence charSequence, String s) {
            return charSequence.toString().equals(s);
        }
    }

}
