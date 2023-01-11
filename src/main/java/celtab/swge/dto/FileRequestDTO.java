package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

    private String format;

    private MultipartFile[] file;
}
