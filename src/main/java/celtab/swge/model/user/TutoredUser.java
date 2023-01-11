package celtab.swge.model.user;

import celtab.swge.model.File;
import celtab.swge.model.enrollment.CaravanTutoredEnrollment;
import celtab.swge.model.registration.TutoredRegistration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class TutoredUser extends BasicUser {

    @Column(nullable = false, unique = true)
    private String idNumber;

    @Column(nullable = false)
    private String country;

    @ManyToOne
    @JoinColumn(name = "authorization_id")
    private File authorization;

    @OneToMany(mappedBy = "tutoredUser")
    private List<CaravanTutoredEnrollment> caravanTutoredEnrollments;

    @OneToMany(mappedBy = "tutoredUser")
    private List<TutoredRegistration> tutoredRegistrations;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @OneToMany(mappedBy = "tutoredUser")
    private List<Exclusion> exclusionRequests;
}
