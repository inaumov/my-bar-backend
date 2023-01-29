package mybar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class WebProperties {

    @Value("${http.sec.path}")
    private String path;

    @Value("${http.oauthPath}")
    private String oauthPath;

    @Autowired
    private CommonPaths commonPaths;

    public WebProperties() {
        super();
    }

    // API

    public String getPath() {
        return path;
    }

    public String getOauthPath() {
        return oauthPath;
    }

    public String getProtocol() {
        return commonPaths.getProtocol();
    }

    public String getHost() {
        return commonPaths.getHost();
    }

    public int getPort() {
        return commonPaths.getPort();
    }

}
