package celtab.swge.auth.oauth2;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract void setEmail(String email);

    public abstract String getImageUrl();

    protected void applyAttributesValue(String key, Object value) {
        var newAttributes = new HashMap<>(attributes);
        newAttributes.put(key, value);
        attributes = newAttributes;
    }

}
