package celtab.swge.model.registration.individual_registration;

import celtab.swge.model.Activity;
import celtab.swge.model.registration.Registration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class IndividualRegistration extends BasicIndividualRegistration {

    @ManyToOne
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @OneToMany(mappedBy = "individualRegistration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndividualRegistrationSchedule> individualRegistrationScheduleList;

    @Column(nullable = false)
    private Boolean customSchedule;

    public IndividualRegistration(Activity activity, Registration registration) {
        setActivity(activity);
        this.registration = registration;
    }

    @PrePersist
    @PreUpdate
    void updateCustomSchedule() {
        if (this.customSchedule == null)
            this.customSchedule = false;
    }
}
