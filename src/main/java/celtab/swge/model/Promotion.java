package celtab.swge.model;

import celtab.swge.model.registration.Registration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Promotion extends BasicModel<Long> {

    @ManyToOne
    @JoinColumn(name = "registration_type_id")
    private RegistrationType registrationType;

    @Column(nullable = false)
    private Double percentage;

    @Column(nullable = false)
    private Date initialDateTime;

    @Column(nullable = false)
    private Date finalDateTime;

    private Integer vacancies;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isVoucher;

    @Transient
    private Integer remainingVacancies;

    @OneToMany(mappedBy = "promotion")
    private List<Registration> registrations;

    @PostLoad
    private void loadRemainingVacancies() {
        if (vacancies == null) return;
        remainingVacancies = vacancies - registrations.size();
    }
}
