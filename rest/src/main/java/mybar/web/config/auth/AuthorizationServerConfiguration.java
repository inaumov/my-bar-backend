package mybar.web.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private AuthenticationManager authenticationManager;

    @Autowired
    public AuthorizationServerConfiguration(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public AuthorizationServerConfiguration() {
        super();
    }

    // beans

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    // config

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("api-test")
                .secret("bGl2ZS10ZXN0")
                .authorizedGrantTypes("password")
                .scopes("my-bar")
                .autoApprove("my-bar")
                .accessTokenValiditySeconds(3600)
                .and()
                .withClient("my-bar-app")
                .secret("secret001")
                .authorizedGrantTypes("password")
                .scopes("my-bar")
                .autoApprove("my-bar")
                .accessTokenValiditySeconds(3600);
    }

}
