package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaravanTutoredEnrollmentRequestDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "caravan_id")
    private CaravanRequestDTO caravan;

    @Column(nullable = false)
    private Boolean accepted;

    @Column(nullable = false)
    private Boolean payed;

    @ManyToOne
    @JoinColumn(name = "tutored_user_id")
    private TutoredUserRequestDTO tutoredUser;

}
