package celtab.swge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String number;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    @JsonIgnoreProperties(value = {"spaces"})
    @ToString.Exclude
    private InstitutionResponseDTO institution;

}
