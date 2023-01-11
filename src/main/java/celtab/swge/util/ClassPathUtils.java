package celtab.swge.util;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public interface ClassPathUtils {

    default String getContentFromClassPath(String path) throws IOException {
        var resource = new ClassPathResource(path);
        var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        return reader.lines().collect(Collectors.joining());
    }

    default ClassPathResource getResourceFromClassPath(String path) throws IOException {
        return new ClassPathResource(path);
    }
}
