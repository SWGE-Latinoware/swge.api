package celtab.swge.dto;

import celtab.swge.model.enums.CaravanType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaravanResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "coordinator_id")
    private UserResponseDTO coordinator;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private EditionResponseDTO edition;

    @Column(nullable = false)
    private Integer vacancies;

    @Transient
    private Integer remainingVacancies;

    @Column(nullable = false)
    private Double price;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institution_id")
    private InstitutionResponseDTO institution;

    @Column(nullable = false)
    private String country;

    private String state;

    private String city;

    @Column(nullable = false)
    private Boolean payed;

    @Column(nullable = false)
    private CaravanType type;

    @JsonIgnoreProperties(value = {"caravan"})
    private List<CaravanEnrollmentResponseDTO> caravanEnrollments;

    @JsonIgnoreProperties(value = {"caravan"})
    private List<CaravanTutoredEnrollmentResponseDTO> caravanTutoredEnrollments;

    @JsonIgnoreProperties(value = {"caravan"})
    private List<NoticeResponseDTO> notices;

}
