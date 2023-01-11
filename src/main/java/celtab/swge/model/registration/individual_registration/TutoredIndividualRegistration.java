package celtab.swge.model.registration.individual_registration;

import celtab.swge.model.Activity;
import celtab.swge.model.registration.TutoredRegistration;
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
public class TutoredIndividualRegistration extends BasicIndividualRegistration {

    @ManyToOne
    @JoinColumn(name = "registration_id")
    private TutoredRegistration registration;

    public TutoredIndividualRegistration(Activity activity, TutoredRegistration registration) {
        setActivity(activity);
        this.registration = registration;
    }
}
