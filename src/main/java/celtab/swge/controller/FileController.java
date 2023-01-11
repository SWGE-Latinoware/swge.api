package celtab.swge.controller;

import celtab.swge.dto.FileRequestDTO;
import celtab.swge.dto.FileResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.File;
import celtab.swge.service.FileService;
import celtab.swge.util.ClassPathUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URLConnection;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/files")
public class FileController extends GenericController<File, Long, FileRequestDTO, FileResponseDTO> implements ClassPathUtils {

    private final FileService fileService;

    public FileController(FileService fileService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        super(fileService, modelMapper, objectMapper);
        this.fileService = fileService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Transactional
    public FileResponseDTO createFile(@ModelAttribute FileRequestDTO fileRequestDTO) {
        try {
            var fileInfo = mapTo(fileRequestDTO, File.class);
            fileInfo.setId(null);
            return mapTo(fileService.saveFile(fileInfo, fileRequestDTO.getFile()[0]), FileResponseDTO.class);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Transactional
    public FileResponseDTO updateFile(@ModelAttribute FileRequestDTO fileRequestDTO) {
        try {
            var fileInfo = mapTo(fileRequestDTO, File.class);
            if (fileService.findOne(fileInfo.getId()) == null) {
                throw new ControllerException(NOT_FOUND, "File Not Found!");
            }
            return mapTo(fileService.saveFile(fileInfo, fileRequestDTO.getFile()[0]), FileResponseDTO.class);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Long id) {
        try {
            var resource = fileService.loadFileWithResource(id);
            var fullName = resource.getFileInfo().getName();
            var format = resource.getFileInfo().getFormat();
            if (format != null && !format.isEmpty()) {
                fullName += "." + format;
            }
            return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fullName + "\"")
                .body(resource.getFile());
        } catch (Exception e) {
            throw new ControllerException(NOT_FOUND, "File '" + id + "' Not Found!");
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void deleteFile(@PathVariable Long id) {
        try {
            fileService.removeFile(id);
        } catch (Exception e) {
            throw new ControllerException(CONFLICT, "File '" + id + "' Not Deleted!");
        }
    }

    @GetMapping(value = "/terms/{termName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getTerm(@PathVariable String termName) {
        try {
            if (termName.equals("authorization-term")) {
                return Base64.encodeBase64(getResourceFromClassPath("terms/" + termName + ".pdf").getInputStream().readAllBytes());
            }
            return getResourceFromClassPath("terms/" + termName + ".html").getInputStream().readAllBytes();
        } catch (Exception e) {
            throw new ControllerException(NOT_FOUND, "The requested Term was not found");
        }
    }

    @GetMapping("/images/{id}")
    public @ResponseBody ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        try {
            var resource = fileService.loadFileWithResource(id);
            var fullName = resource.getFileInfo().getName();
            var format = resource.getFileInfo().getFormat();
            if (format != null && !format.isEmpty()) {
                fullName += "." + format;
            }
            return ResponseEntity.ok().contentType(MediaType.valueOf(URLConnection.guessContentTypeFromName(fullName))).body(resource.getFile().getInputStream().readAllBytes());
        } catch (Exception e) {
            throw new ControllerException(NOT_FOUND, "File '" + id + "' Not Found!");
        }
    }

    @GetMapping(value = "/images/svg/{imageName}", produces = "image/svg+xml")
    public @ResponseBody byte[] getSvgImage(@PathVariable String imageName) {
        try {
            return getResourceFromClassPath(String.format("mail/images/%s.svg", imageName)).getInputStream().readAllBytes();
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, "Something went wrong on image GET Endpoint");
        }
    }

    @GetMapping(value = "/images/png/{imageName}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getPngImage(@PathVariable String imageName) {
        try {
            return getResourceFromClassPath(String.format("mail/images/%s.png", imageName)).getInputStream().readAllBytes();
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, "Something went wrong on image GET Endpoint");
        }
    }
}
