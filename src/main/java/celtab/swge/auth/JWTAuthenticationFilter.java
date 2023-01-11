package celtab.swge.auth;

import celtab.swge.exception.AuthenticationExceptionConcrete;
import celtab.swge.property.JWTProperties;
import celtab.swge.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter implements JWTTokenGenerator {

    private final AuthenticationManager authenticationManager;

    private final JWTProperties jwtProperties;

    private final UserService userService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTProperties jwtProperties, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtProperties = jwtProperties;
        this.userService = userService;
        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            var creds = new ObjectMapper().readValue(req.getInputStream(), JWTUser.class);
            var user = userService.findByEmail(creds.getEmail());
            if (user == null) {
                throw new BadCredentialsException("Bad credentials");
            }
            if (!Boolean.TRUE.equals(user.getConfirmed()) || !Boolean.TRUE.equals(user.getEnabled())) {
                throw new AuthenticationExceptionConcrete("Email not confirmed or user not enabled");
            }
            if (Boolean.TRUE.equals(creds.getRememberMe())) {
                return authenticationManager.authenticate(
                    new JWTUsernamePasswordAuthenticationToken(
                        creds.getEmail(),
                        creds.getPassword(),
                        new ArrayList<>(),
                        creds)
                );
            }
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    creds.getEmail(),
                    creds.getPassword(),
                    new ArrayList<>())
            );
        } catch (Exception e) {
            throw new AuthenticationExceptionConcrete(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {
        var details = auth.getDetails();

        var user = (User) auth.getPrincipal();
        var loggedUser = userService.findByEmail(user.getUsername());
        loggedUser.setLastLogin(Date.from(Instant.now()));
        userService.save(loggedUser);

        var time = new Date(System.currentTimeMillis() + (details instanceof JWTUser ? jwtProperties.getRememberMe() : jwtProperties.getExpirationTime()));
        var token = getToken(auth, time, jwtProperties);
        res.getWriter().write(token);
        res.getWriter().flush();
    }

}
