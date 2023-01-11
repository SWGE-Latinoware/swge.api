package celtab.swge.model;

import celtab.swge.model.enrollment.CaravanEnrollment;
import celtab.swge.model.enrollment.CaravanTutoredEnrollment;
import celtab.swge.model.enums.CaravanType;
import celtab.swge.model.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "edition_id"})
})
public class Caravan extends BasicModel<Long> {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "coordinator_id")
    private User coordinator;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;

    @Column(nullable = false)
    private Integer vacancies;

    @Transient
    private Integer remainingVacancies;

    @Column(nullable = false)
    private Double price;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @Column(nullable = false)
    private String country;

    private String state;

    private String city;

    @Column(nullable = false)
    private Boolean payed;

    @Column(nullable = false)
    private CaravanType type;

    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "caravan")
    private List<CaravanEnrollment> caravanEnrollments;

    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "caravan")
    private List<CaravanTutoredEnrollment> caravanTutoredEnrollments;

    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "caravan")
    private List<Notice> notices;

    @PostLoad
    private void loadRemainingVacancies() {
        Integer remaining;
        try {
            switch (type) {
                case GENERAL:
                    remaining = caravanEnrollments.stream().mapToInt(caravanEnrollment ->
                        Boolean.TRUE.equals(caravanEnrollment.getAccepted()) &&
                            Boolean.TRUE.equals(caravanEnrollment.getConfirmed()) ? 1 : 0
                    ).reduce(0, Math::addExact);
                    break;
                case TUTORED:
                    remaining = caravanTutoredEnrollments.stream().mapToInt(caravanEnrollment ->
                        Boolean.TRUE.equals(caravanEnrollment.getAccepted()) ? 1 : 0
                    ).reduce(0, Math::addExact);
                    break;
                default:
                    remaining = null;
                    break;
            }
        } catch (Exception e) {
            return;
        }
        remainingVacancies = vacancies - remaining;
    }
}
