package mybar.rest;

import io.restassured.RestAssured;
import io.restassured.authentication.OAuthSignature;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import mybar.ScriptExecutor;
import mybar.JsonUtil;
import mybar.OAuthAuthenticator;
import mybar.spring.ApiTestsContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApiTestsContextConfiguration.class}, loader = AnnotationConfigContextLoader.class)
@Slf4j
public abstract class ApiTest {

    protected final JsonUtil jsonUtil = new JsonUtil();

    @Autowired
    private OAuthAuthenticator authenticator;
    @Autowired
    private ScriptExecutor service;

    protected RequestSpecification givenAuthenticated(String username, String password) {

        final String accessToken = authenticator.getAccessToken(username, password);

        return RestAssured
                .given()
                .auth()
                .oauth2(accessToken, OAuthSignature.HEADER);
    }

    protected void runSql(String... fileNames) {
        log.debug("The application is about to execute SQL(s): {}", Arrays.toString(fileNames));
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Arrays.stream(fileNames)
                .map(resolver::getResource)
                .forEach(resource -> service.runScript(resource));
    }

}
