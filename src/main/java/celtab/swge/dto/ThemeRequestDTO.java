package celtab.swge.dto;

import celtab.swge.model.enums.ThemeType;
import celtab.swge.util.HashMapConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThemeRequestDTO extends GenericDTO<Long> {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private ThemeType type;

    @Column(nullable = false, columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> colorPalette;
}
