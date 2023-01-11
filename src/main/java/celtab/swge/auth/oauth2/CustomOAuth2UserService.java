package celtab.swge.auth.oauth2;

import celtab.swge.exception.AuthenticationExceptionConcrete;
import celtab.swge.model.File;
import celtab.swge.model.user.User;
import celtab.swge.security.SecurityRoles;
import celtab.swge.security.SecurityRoles.SecurityRole;
import celtab.swge.service.FileService;
import celtab.swge.service.UserService;
import celtab.swge.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService implements OAuth2UserInfoFactory, UUIDUtils {

    private static final String OBJECT_NOT_RECOGNIZED_MSG = "Info object not recognized!";

    private final UserService userService;

    private final FileService fileService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        var oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private User getUser(OAuth2UserInfo oAuth2UserInfo) throws AuthenticationExceptionConcrete {
        User user;
        if (oAuth2UserInfo.getClass().isAssignableFrom(GoogleOAuth2UserInfo.class)) {
            user = userService.findByGoogleId(oAuth2UserInfo.getId());
        } else if (oAuth2UserInfo.getClass().isAssignableFrom(GitHubOAuth2UserInfo.class)) {
            user = userService.findByGithubId(oAuth2UserInfo.getId());
        } else {
            throw new AuthenticationExceptionConcrete(OBJECT_NOT_RECOGNIZED_MSG);
        }
        if (user == null) {
            if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isBlank()) {
                return null;
            }
            return userService.findByEmail(oAuth2UserInfo.getEmail());
        }
        return user;
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        var oAuth2UserInfo = getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (oAuth2UserInfo.getId() == null || oAuth2UserInfo.getId().isBlank()) {
            throw new AuthenticationExceptionConcrete("Social Account ID not provided!");
        }
        var user = getUser(oAuth2UserInfo);
        if (user != null) {
            oAuth2UserInfo.setEmail(user.getEmail());
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isBlank()) {
                oAuth2UserInfo.setEmail(generateFakeTempEmailForJwt());
            }
            user = registerNewUser(oAuth2UserInfo);
        }
        if (!Boolean.TRUE.equals(user.getConfirmed()) || !Boolean.TRUE.equals(user.getEnabled())) {
            throw new AuthenticationExceptionConcrete("Email not confirmed or user not enabled");
        }
        var roles = new ArrayList<SecurityRole>();
        if (Boolean.TRUE.equals(user.getAdmin())) {
            roles.add(SecurityRoles.ADMINISTRATOR_ROLE);
        }
        return new DefaultOAuth2User(roles, oAuth2UserInfo.getAttributes(), "email");
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo) throws AuthenticationExceptionConcrete {
        try {
            var user = new User();
            var oauthName = oAuth2UserInfo.getName();
            if (oauthName == null) {
                oauthName = generateFakeTempName();
                user.setName(oauthName);
                user.setTagName(oauthName);
            } else {
                user.setName(oAuth2UserInfo.getName());
                user.setTagName(oAuth2UserInfo.getName());
            }
            user.setEmail(oAuth2UserInfo.getEmail());
            var defaultPassword = getRandomUUIDString();
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setAdmin(false);
            user.setEmailCommunication(false);
            user.setEnabled(true);
            user.setUserPermissions(Collections.emptyList());
            user.setSocialCommunication(Boolean.FALSE);

            if (oAuth2UserInfo.getImageUrl() != null && !oAuth2UserInfo.getImageUrl().isBlank()) {

                var inputStream = new URL(oAuth2UserInfo.getImageUrl()).openStream();
                var userProfile = new File();

                userProfile.setName("userProfile");
                userProfile.setFormat("png");

                user.setUserProfile(fileService.saveFile(userProfile, inputStream));
            }

            if (oAuth2UserInfo.getClass().isAssignableFrom(GoogleOAuth2UserInfo.class)) {
                var googleInfo = (GoogleOAuth2UserInfo) oAuth2UserInfo;
                user.setConfirmed(googleInfo.getEmailVerified());
                user.setGoogleId(googleInfo.getId());
                return userService.save(user);
            }
            if (oAuth2UserInfo.getClass().isAssignableFrom(GitHubOAuth2UserInfo.class)) {
                var githubInfo = (GitHubOAuth2UserInfo) oAuth2UserInfo;
                user.setConfirmed(true);
                user.setGithub(githubInfo.getUrl());
                user.setGithubId(githubInfo.getId());
                return userService.save(user);
            }
            throw new AuthenticationExceptionConcrete(OBJECT_NOT_RECOGNIZED_MSG);
        } catch (AuthenticationExceptionConcrete e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationExceptionConcrete("It was not possible to create the User!");
        }
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) throws AuthenticationExceptionConcrete {
        try {
            if (oAuth2UserInfo.getClass().isAssignableFrom(GoogleOAuth2UserInfo.class)) {
                var googleInfo = (GoogleOAuth2UserInfo) oAuth2UserInfo;
                existingUser.setConfirmed(googleInfo.getEmailVerified());
                existingUser.setGoogleId(googleInfo.getId());
                return userService.save(existingUser);
            }
            if (oAuth2UserInfo.getClass().isAssignableFrom(GitHubOAuth2UserInfo.class)) {
                var githubInfo = (GitHubOAuth2UserInfo) oAuth2UserInfo;
                existingUser.setGithubId(githubInfo.getId());
                return userService.save(existingUser);
            }
            throw new AuthenticationExceptionConcrete(OBJECT_NOT_RECOGNIZED_MSG);
        } catch (AuthenticationExceptionConcrete e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationExceptionConcrete("It was not possible to update the User!");
        }
    }

}
