package celtab.swge.dto;

import celtab.swge.model.enums.CaravanType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaravanRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "coordinator_id")
    private UserRequestDTO coordinator;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private EditionRequestDTO edition;

    @Column(nullable = false)
    private Integer vacancies;

    @Column(nullable = false)
    private Double price;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institution_id")
    private InstitutionRequestDTO institution;

    @Column(nullable = false)
    private String country;

    private String state;

    private String city;

    @Column(nullable = false)
    private Boolean payed;

    @Column(nullable = false)
    private CaravanType type;

}
