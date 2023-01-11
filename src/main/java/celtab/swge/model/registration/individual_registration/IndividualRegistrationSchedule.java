package celtab.swge.model.registration.individual_registration;

import celtab.swge.model.BasicModel;
import celtab.swge.model.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class IndividualRegistrationSchedule extends BasicModel<Long> {

    @ManyToOne
    @JoinColumn(name = "individual_registration_id", nullable = false)
    private IndividualRegistration individualRegistration;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;
}
