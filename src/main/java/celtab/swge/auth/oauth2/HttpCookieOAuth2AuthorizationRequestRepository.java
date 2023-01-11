package celtab.swge.auth.oauth2;

import celtab.swge.property.OAuth2Properties;
import celtab.swge.util.CookieUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest>, CookieUtils {

    private final OAuth2Properties oauth2Properties;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpServletRequest) {
        return getCookie(httpServletRequest, oauth2Properties.getRequestCookieName())
            .map(cookie -> deserialize(cookie, OAuth2AuthorizationRequest.class))
            .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (oAuth2AuthorizationRequest == null) {
            deleteCookie(httpServletRequest, httpServletResponse, oauth2Properties.getRequestCookieName());
            deleteCookie(httpServletRequest, httpServletResponse, oauth2Properties.getRedirectUriParam());
            return;
        }
        addCookie(httpServletResponse, oauth2Properties.getRequestCookieName(), serialize(oAuth2AuthorizationRequest), oauth2Properties.getExpirationTime());
        var redirectUriAfterLogin = httpServletRequest.getParameter(oauth2Properties.getRedirectUriParam());
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            addCookie(httpServletResponse, oauth2Properties.getRedirectUriParam(), redirectUriAfterLogin, oauth2Properties.getExpirationTime());
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest httpServletRequest) {
        return loadAuthorizationRequest(httpServletRequest);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        deleteCookie(httpServletRequest, httpServletResponse, oauth2Properties.getRequestCookieName());
        deleteCookie(httpServletRequest, httpServletResponse, oauth2Properties.getRedirectUriParam());
    }

}
