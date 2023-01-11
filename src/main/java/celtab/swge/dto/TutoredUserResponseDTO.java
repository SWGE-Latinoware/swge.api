package celtab.swge.dto;

import celtab.swge.model.enums.SpecialNeedsType;
import celtab.swge.util.HashMapConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TutoredUserResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tagName;

    @Column(nullable = false)
    private String country;

    private String cellPhone;

    private Date birthDate;

    @Column(nullable = false, unique = true)
    private String idNumber;

    private String otherNeeds;

    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private List<SpecialNeedsType> needsTypes;

    @ManyToOne
    @JoinColumn(name = "authorization_id")
    private FileResponseDTO authorization;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    @ToString.Exclude
    private UserResponseDTO reviewer;
}
