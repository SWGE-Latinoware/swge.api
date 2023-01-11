package celtab.swge.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "oauth2")
@Getter
@Setter
public class OAuth2Properties {

    private Integer expirationTime;

    private String requestCookieName;

    private String redirectUriParam;

    private List<String> authorizedRedirectUris;

}
