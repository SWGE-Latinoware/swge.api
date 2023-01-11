package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class IndividualRegistrationScheduleResponseDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "individual_registration_id", nullable = false)
    @ToString.Exclude
    private IndividualRegistrationResponseDTO individualRegistration;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private ScheduleResponseDTO schedule;
}
