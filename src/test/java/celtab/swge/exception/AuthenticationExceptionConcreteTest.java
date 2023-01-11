package celtab.swge.exception;

import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AuthenticationExceptionConcreteTest extends GenericTestService {

    @Test
    void AthenticationExceptionConcreteShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                new AuthenticationExceptionConcrete("Exception");
            }
        );
    }
}
