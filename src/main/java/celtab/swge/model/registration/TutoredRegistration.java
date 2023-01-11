package celtab.swge.model.registration;

import celtab.swge.model.registration.individual_registration.TutoredIndividualRegistration;
import celtab.swge.model.user.TutoredUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class TutoredRegistration extends BasicRegistration {

    @ManyToOne
    @JoinColumn(name = "tutored_user_id")
    private TutoredUser tutoredUser;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutoredIndividualRegistration> individualRegistrations;

}
