package celtab.swge.property;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JWTProperties {

    private String authorization;

    private String bearer;

    private String secret;

    private Integer expirationTime;

    private Integer rememberMe;

    private Integer refreshTime;

    public String getBearer() {
        return bearer.replace("\"", "");
    }
}
