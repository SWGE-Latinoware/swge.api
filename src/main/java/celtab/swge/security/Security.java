package celtab.swge.security;

import celtab.swge.auth.JWTAuthenticationFilter;
import celtab.swge.auth.JWTAuthorizationFilter;
import celtab.swge.auth.UserDetailsServiceImpl;
import celtab.swge.auth.oauth2.CustomOAuth2UserService;
import celtab.swge.auth.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import celtab.swge.auth.oauth2.OAuth2AuthenticationFailureHandler;
import celtab.swge.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import celtab.swge.property.JWTProperties;
import celtab.swge.property.OAuth2Properties;
import celtab.swge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@Configuration
@Profile("security")
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;

    private final JWTProperties jwtProperties;

    private final UserService userService;

    private final OAuth2Properties oauth2Properties;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurity webSecurity() {
        return new WebSecurity();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository(oauth2Properties);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(POST, "/api/login").permitAll()
            .antMatchers("/api/oauth2/**").permitAll()
            .antMatchers(POST, "/api/users/auto-registration").permitAll()
            .antMatchers(GET, "/api/users/unique/**").permitAll()
            .antMatchers(POST, "/api/users/reset-password").permitAll()
            .antMatchers(POST, "/api/users/email-confirmation/email").permitAll()
            .antMatchers(GET, "/api/users/validate/email-confirmation").permitAll()
            .antMatchers(GET, "/api/users/validate/user-disabled").permitAll()
            .antMatchers(GET, "/api/institutions/list").permitAll()
            .antMatchers(GET, "/api/editions/list").permitAll()
            .antMatchers(GET, "/api/editions/{id}/info/**").permitAll()
            .antMatchers(GET, "/api/editions/{id}/languageContent").permitAll()
            .antMatchers(GET, "/api/editions/{id}/languageContent/**").permitAll()
            .antMatchers(GET, "/api/location/**").permitAll()
            .antMatchers(GET, "/api/fonts/**").permitAll()
            .antMatchers(GET, "/api/files/**").permitAll()
            .antMatchers(GET, "/api/files/images/**").permitAll()
            .antMatchers(GET, "/api/info/**").permitAll()
            .antMatchers(GET, "/api/flux/**").permitAll()
            .antMatchers(GET, "/api/themes/list").permitAll()
            .antMatchers(GET, "/api/registrations/payment/public-key").permitAll()
            .antMatchers("/api/themes/**").hasAuthority(SecurityRoles.ADMINISTRATOR_ROLE_VALUE)
            .antMatchers(GET, "/api/url/**").permitAll()
            .antMatchers("/api/user-permissions/**").hasAuthority(SecurityRoles.ADMINISTRATOR_ROLE_VALUE)
            .antMatchers("/api/registration-types/**").hasAuthority(SecurityRoles.ADMINISTRATOR_ROLE_VALUE)
            .anyRequest()
            .authenticated()
            .and()
            .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtProperties, userService))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtProperties, userService))
            .exceptionHandling()
            .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
            .and()
            .sessionManagement()
            .sessionCreationPolicy(STATELESS)
            .and()
            .oauth2Login()
            .authorizationEndpoint()
            .baseUri("/api/oauth2/authorize")
            .authorizationRequestRepository(cookieAuthorizationRequestRepository())
            .and()
            .redirectionEndpoint()
            .baseUri("/api/oauth2/callback/*")
            .and()
            .userInfoEndpoint()
            .userService(customOAuth2UserService)
            .and()
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

}
