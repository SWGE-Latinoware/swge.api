package celtab.swge.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

public class SecurityRoles {

    private SecurityRoles() {

    }

    @RequiredArgsConstructor
    public static class SecurityRole implements GrantedAuthority {

        private final String roleName;

        @Override
        public String getAuthority() {
            return roleName;
        }
    }

    public static final String ADMINISTRATOR_ROLE_VALUE = "ADMINISTRATOR";

    public static final SecurityRole ADMINISTRATOR_ROLE = new SecurityRole(ADMINISTRATOR_ROLE_VALUE);
}
