package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class IndividualRegistrationScheduleRequestDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "individual_registration_id", nullable = false)
    private IndividualRegistrationRequestDTO individualRegistration;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private ScheduleRequestDTO schedule;
}
