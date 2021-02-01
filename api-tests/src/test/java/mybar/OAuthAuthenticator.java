package mybar;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@Slf4j
public class OAuthAuthenticator {

    private static final String CLIENT_ID = "api-test";
    private static final String CLIENT_SECRET = "bGl2ZS10ZXN0";

    @Autowired
    private WebProperties webProps;

    public OAuthAuthenticator() {
        super();
    }

    // API

    public final String getAccessToken(final String username, final String password) {
        try {
            final URI uri = new URI(webProps.getProtocol(), null, webProps.getHost(), webProps.getPort(), webProps.getPath() + webProps.getOauthPath(), null, null);
            final String url = uri.toString();
            final String encodedCredentials = new String(Base64.encodeBase64((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));

            final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "password");
            params.add("client_id", CLIENT_ID);
            params.add("username", username);
            params.add("password", password);

            final HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + encodedCredentials);

            final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            final RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(new StringHttpMessageConverter());

            final TokenResponse tokenResponse = restTemplate.postForObject(url, request, TokenResponse.class);
            String accessToken = tokenResponse.getAccessToken();
            log.info("An access token has been obtained = {}", accessToken);
            return accessToken;
        } catch (final HttpClientErrorException e) {
            log.warn("", e);
            log.info("Full Body = {}", e.getResponseBodyAsString());
        } catch (final URISyntaxException e) {
            log.warn("", e);
        }

        return null;
    }

}
