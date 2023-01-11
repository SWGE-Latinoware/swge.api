package celtab.swge.controller;

import celtab.swge.dto.FileRequestDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.service.URLService;
import celtab.swge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static celtab.swge.model.enums.URLType.EMAIL_CONFIRMATION;

@RequiredArgsConstructor
@RestController
@RequestMapping("/url")
public class URLController {

    private final URLService urlService;

    private final UserService userService;

    @GetMapping("/{url}")
    @Transactional
    public void processUrl(@PathVariable String url) {
        var realUrl = urlService.findByURL(url);
        if (realUrl == null) {
            throw new ControllerException(HttpStatus.NOT_FOUND, "URL " + url + " Not Found!");
        }
        try {
            if (realUrl.getType() == EMAIL_CONFIRMATION) {
                if (Objects.equals(realUrl.getUser().getEmail(), realUrl.getEmail())) {
                    realUrl.getUser().setConfirmed(true);
                    userService.save(realUrl.getUser());
                    return;
                }
                throw new ControllerException(HttpStatus.CONFLICT, "Email does not match the User's email!");
            }
            throw new ControllerException(HttpStatus.BAD_REQUEST, "URL with invalid type!");
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(value = "/hash-validation/{urlFragment}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Boolean validateHash(@PathVariable String urlFragment, @ModelAttribute FileRequestDTO fileRequestDTO) {

        var url = urlService.findByURL(urlFragment);
        if (url == null) {
            throw new ControllerException(HttpStatus.NOT_FOUND, "URL Fragment not found!");
        }

        try {
            var hash = Arrays.hashCode(fileRequestDTO.getFile()[0].getBytes());

            return url.getHash() == hash;

        } catch (IOException e) {

            throw new ControllerException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
