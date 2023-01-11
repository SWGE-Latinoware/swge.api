package celtab.swge.dto;

import celtab.swge.util.HashMapConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class EditionHomeRequestDTO extends GenericDTO<Long>{

    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> homeContent;

    @Column(nullable = false)
    private EditionRequestDTO edition;

    @Column(nullable = false)
    private String language;

}
