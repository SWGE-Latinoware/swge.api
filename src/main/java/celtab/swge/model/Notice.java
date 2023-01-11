package celtab.swge.model;

import celtab.swge.util.HashMapConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Notice extends BasicModel<Long> {

    @Column(nullable = false)
    private Date dateTime;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(nullable = false, columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> description;

    @ManyToOne
    @JoinColumn(name = "caravan_id")
    private Caravan caravan;
}
