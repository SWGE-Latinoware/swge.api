package celtab.swge.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public JWTUsernamePasswordAuthenticationToken(
        Object principal,
        Object credentials,
        Collection<? extends GrantedAuthority> authorities,
        Object details
    ) {
        super(principal, credentials, authorities);
        setDetails(details);
    }
}
