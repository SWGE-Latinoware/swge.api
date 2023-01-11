package celtab.swge.model.registration.individual_registration;

import celtab.swge.model.Activity;
import celtab.swge.model.BasicModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class BasicIndividualRegistration extends BasicModel<Long> {

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

}
