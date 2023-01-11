package celtab.swge.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
@Component
public class EnvironmentInfo {

    private final Environment environment;

    public boolean isTest() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }

}
