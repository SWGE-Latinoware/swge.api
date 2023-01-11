package celtab.swge.dto;

import celtab.swge.util.HashMapConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoticeRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private Date dateTime;

    @Column(nullable = false, columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> description;

    @ManyToOne
    @JoinColumn(name = "caravan_id")
    private CaravanRequestDTO caravan;
}
