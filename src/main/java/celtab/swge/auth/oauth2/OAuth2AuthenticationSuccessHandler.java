package celtab.swge.auth.oauth2;

import celtab.swge.auth.JWTTokenGenerator;
import celtab.swge.exception.ControllerException;
import celtab.swge.property.JWTProperties;
import celtab.swge.property.OAuth2Properties;
import celtab.swge.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements JWTTokenGenerator, CookieUtils {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    private final OAuth2Properties oauth2Properties;

    private final JWTProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        var targetUrl = determineTargetUrl(httpServletRequest, httpServletResponse, authentication);
        clearAuthenticationAttributes(httpServletRequest, httpServletResponse);
        if (httpServletResponse.isCommitted()) return;
        getRedirectStrategy().sendRedirect(httpServletRequest, httpServletResponse, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        var redirectUri = getCookie(httpServletRequest, oauth2Properties.getRedirectUriParam()).map(Cookie::getValue);
        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, "It was not possible to process the URI: " + redirectUri.get());
        }
        var targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        var token = getToken(authentication, Date.from(Instant.now().plusMillis(oauth2Properties.getExpirationTime())), jwtProperties);
        return UriComponentsBuilder.fromUriString(targetUrl + "/" + token + "/false").build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super.clearAuthenticationAttributes(httpServletRequest);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(httpServletRequest, httpServletResponse);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        var clientRedirectUri = URI.create(uri);
        return oauth2Properties.getAuthorizedRedirectUris()
            .stream()
            .anyMatch(authorizedRedirectUri -> {
                var authorizedURI = URI.create(authorizedRedirectUri);
                return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedURI.getPort() == clientRedirectUri.getPort();
            });
    }

}
