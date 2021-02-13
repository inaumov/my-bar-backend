package mybar.web.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ResourceServerConfiguration(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        super();
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    // global security concerns

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
    }

    // http security concerns

    @Override
    public void configure(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/menu").permitAll()
                .antMatchers("/cocktails/**").hasRole("USER")
                .antMatchers("/ingredients/**").hasRole("USER")
                .antMatchers("/shelf/**").hasRole("USER")
                .antMatchers("/rates/history").hasRole("ANALYST")
                .antMatchers("/rates/**").hasRole("USER")
                .antMatchers("/users/**").hasRole("ADMIN")
                .antMatchers("/api/bar/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable();
    }

}
