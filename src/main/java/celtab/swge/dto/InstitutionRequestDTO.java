package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class InstitutionRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    private String shortName;

    private String country;

    private String state;

    private String city;

    private String cellPhone;

    private String phone;

    private String website;

    @OneToMany(mappedBy = "institution", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<SpaceRequestDTO> spaces;

}
