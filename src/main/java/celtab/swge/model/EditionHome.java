package celtab.swge.model;

import celtab.swge.util.HashMapConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class EditionHome extends BasicModel<Long> {

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> homeContent;

    @Column(nullable = false)
    private String language;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;
}
