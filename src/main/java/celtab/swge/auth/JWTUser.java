package celtab.swge.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTUser {

    private String email;

    private String password;

    private Boolean rememberMe;
}
