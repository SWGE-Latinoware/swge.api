package celtab.swge.exception;

public class AuthenticationExceptionConcrete extends org.springframework.security.core.AuthenticationException {

    public AuthenticationExceptionConcrete(String msg) {
        super(msg);
    }

}
