package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

    private String format;

}
