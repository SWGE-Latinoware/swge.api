package celtab.swge.util;

import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public interface CookieUtils {

    default Optional<Cookie> getCookie(HttpServletRequest httpServletRequest, String name) {
        var cookies = httpServletRequest.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (var cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    default String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    default <T> T deserialize(Cookie cookie, Class<T> klass) {
        return klass.cast(SerializationUtils.deserialize(
            Base64.getUrlDecoder().decode(cookie.getValue())));
    }

    default void deleteCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String name) {
        if (httpServletRequest != null && httpServletRequest.getCookies() != null) {
            Arrays
                .stream(httpServletRequest.getCookies())
                .forEach(cookie -> {
                    if (cookie.getName().equals(name)) {
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        httpServletResponse.addCookie(cookie);
                    }
                });
        }
    }

    default void addCookie(HttpServletResponse httpServletResponse, String name, String value, int maxAgeWithinMilliseconds) {
        var cookie = new Cookie(name, value);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAgeWithinMilliseconds / 1000);
        httpServletResponse.addCookie(cookie);
    }
}
