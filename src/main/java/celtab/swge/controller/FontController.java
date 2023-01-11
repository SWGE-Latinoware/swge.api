package celtab.swge.controller;

import celtab.swge.exception.ControllerException;
import celtab.swge.util.ClassPathUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/fonts")
public class FontController implements ClassPathUtils {

    @GetMapping
    public ResponseEntity<ClassPathResource> getFont(@RequestParam String fileName) {
        try {
            var resource = getResourceFromClassPath("fonts/" + fileName);

            resource.getFile();
            
            return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "font/" + fileName.substring(fileName.length() - 3))
                .body(resource);
        } catch (IOException ie) {
            throw new ControllerException(NOT_FOUND, ie.getMessage());
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }
}
