package celtab.swge.model;

import celtab.swge.util.HashMapConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.Map;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Language extends BasicModel<Long> {

    @Column(nullable = false)
    private String isoCod;

    @Column(nullable = false)
    private String name;

    private String flag;

    @Lob
    @Column(nullable = false)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> translation;

}
