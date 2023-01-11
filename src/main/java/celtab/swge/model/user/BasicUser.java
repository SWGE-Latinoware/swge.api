package celtab.swge.model.user;

import celtab.swge.model.BasicModel;
import celtab.swge.model.enums.SpecialNeedsType;
import celtab.swge.util.SpecialNeedsConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import java.util.Date;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public abstract class BasicUser extends BasicModel<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tagName;

    private String cellPhone;

    private Date birthDate;

    private String otherNeeds;

    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = SpecialNeedsConverter.class)
    private List<SpecialNeedsType> needsTypes;
}
