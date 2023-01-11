package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String number;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private InstitutionRequestDTO institution;

}
