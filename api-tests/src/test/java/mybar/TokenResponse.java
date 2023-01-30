package mybar;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse (

    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("expires_in")
    String expiresIn,

    @JsonProperty("scope")
    String scope
) {
    @Override
    public String toString() {
        return "TokenResponse [accessToken=" + accessToken + ", tokenType=" + tokenType + ", expiresIn=" + expiresIn + ", scope=" + scope + "]";
    }

}