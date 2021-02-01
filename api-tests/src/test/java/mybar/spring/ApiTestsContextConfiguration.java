package mybar.spring;

import mybar.CommonPaths;
import mybar.OAuthAuthenticator;
import mybar.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan({"mybar"})
@PropertySource({"classpath:web-dev.properties"})
public class ApiTestsContextConfiguration {

    @Bean
    public OAuthAuthenticator authenticator() {
        return new OAuthAuthenticator();
    }

    @Bean(name = "webProps")
    public WebProperties webProps() {
        return new WebProperties();
    }

    @Bean(name = "commonPaths")
    public CommonPaths commonPaths() {
        return new CommonPaths();
    }

}
