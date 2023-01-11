package celtab.swge.auth;

import celtab.swge.property.JWTProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Date;

public interface JWTTokenGenerator {

    default String getToken(Authentication auth, Date expirationTime, JWTProperties jwtProperties) {
        if (auth.getPrincipal().getClass().isAssignableFrom(org.springframework.security.core.userdetails.User.class)) {
            return getToken(
                ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername(),
                expirationTime,
                jwtProperties);
        }
        return getToken(
            ((DefaultOAuth2User) auth.getPrincipal()).getName(),
            expirationTime,
            jwtProperties);
    }

    default String getToken(String subject, Date expirationTime, JWTProperties jwtProperties) {
        return JWT.create()
            .withSubject(subject)
            .withExpiresAt(expirationTime)
            .sign(Algorithm.HMAC512(jwtProperties.getSecret().getBytes()));
    }
}
