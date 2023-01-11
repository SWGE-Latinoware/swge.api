package celtab.swge.model;

import celtab.swge.model.enums.ThemeType;
import celtab.swge.util.HashMapConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.Map;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Theme extends BasicModel<Long> {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private ThemeType type;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(nullable = false, columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> colorPalette;

}
