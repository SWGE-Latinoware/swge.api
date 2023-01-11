package celtab.swge.auth;

import celtab.swge.property.JWTProperties;
import celtab.swge.security.SecurityRoles;
import celtab.swge.security.SecurityRoles.SecurityRole;
import celtab.swge.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter implements JWTTokenGenerator {

    private final JWTProperties jwtProperties;

    private final UserService userService;

    public JWTAuthorizationFilter(
        AuthenticationManager authenticationManager,
        JWTProperties jwtProperties,
        UserService userService
    ) {
        super(authenticationManager);
        this.jwtProperties = jwtProperties;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        var header = req.getHeader(jwtProperties.getAuthorization());
        if (header == null || !header.startsWith(jwtProperties.getBearer())) {
            chain.doFilter(req, res);
            return;
        }
        var originalTokenStr = header.replace(jwtProperties.getBearer(), "");
        var originalToken = JWT.require(Algorithm.HMAC512(jwtProperties.getSecret().getBytes()))
            .build()
            .verify(originalTokenStr);
        var token = getToken(
            originalToken.getSubject(),
            Date.from(originalToken.getExpiresAt().toInstant().plusMillis(jwtProperties.getRefreshTime())),
            jwtProperties);
        res.setHeader(jwtProperties.getAuthorization(), jwtProperties.getBearer() + token);
        var authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        var token = request.getHeader(jwtProperties.getAuthorization());
        try {
            if (token != null) {
                var user = JWT.require(Algorithm.HMAC512(jwtProperties.getSecret().getBytes()))
                    .build()
                    .verify(token.replace(jwtProperties.getBearer(), ""))
                    .getSubject();
                if (user != null) {
                    var realUser = userService.findByEmail(user);
                    if (realUser == null) return null;
                    var roles = new ArrayList<SecurityRole>();
                    if (Boolean.TRUE.equals(realUser.getAdmin())) {
                        roles.add(SecurityRoles.ADMINISTRATOR_ROLE);
                    }
                    return new UsernamePasswordAuthenticationToken(user, null, roles);
                }
                return null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
