package celtab.swge.auth.oauth2;

import java.util.Map;

public class GitHubOAuth2UserInfo extends OAuth2UserInfo {

    public GitHubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public void setEmail(String email) {
        applyAttributesValue("email", email);
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }

    public String getCompany() {
        return (String) attributes.get("company");
    }

    public String getBio() {
        return (String) attributes.get("bio");
    }

    public String getLocation() {
        return (String) attributes.get("location");
    }

    public String getUrl() {
        return (String) attributes.get("html_url");
    }
}
