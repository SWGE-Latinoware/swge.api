package celtab.swge.auth.oauth2;

import celtab.swge.exception.AuthenticationExceptionConcrete;
import celtab.swge.model.enums.AuthProvider;
import celtab.swge.util.UUIDUtils;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

public interface OAuth2UserInfoFactory extends UUIDUtils {

    String FAKE_EMAIL_START = "*___TEMP_";
    String FAKE_EMAIL_END = "_TEMP___*";

    default OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) throws AuthenticationException {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())) {
            return new GitHubOAuth2UserInfo(attributes);
        }
        throw new AuthenticationExceptionConcrete("Login method not supported: " + registrationId);
    }

    default String generateFakeTempEmailForJwt() {
        return FAKE_EMAIL_START + getRandomUUIDString() + FAKE_EMAIL_END;
    }

    default String generateFakeTempName() {
        return generateFakeTempEmailForJwt();
    }

    default boolean isFakeTempEmailForJwt(String email) {
        if (email == null) return false;
        return email.startsWith(FAKE_EMAIL_START) && email.endsWith(FAKE_EMAIL_END);
    }

    default boolean isFakeTempName(String name) {
        return isFakeTempEmailForJwt(name);
    }
}
