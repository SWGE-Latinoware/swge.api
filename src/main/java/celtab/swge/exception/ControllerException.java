package celtab.swge.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ControllerException extends ResponseStatusException {
    private final String message;

    public ControllerException(HttpStatus status, String message) {
        super(status, message);
        this.message = message;
    }

}
