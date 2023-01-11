package celtab.swge.model;

import celtab.swge.model.enums.EditionType;
import celtab.swge.model.registration.Registration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Edition extends BasicModel<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String shortName;

    @Column(nullable = false, unique = true)
    private Integer year;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Date initialDate;

    @Column(nullable = false)
    private Date finalDate;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    private String description;

    @Column(nullable = false)
    private EditionType type;

    @ManyToOne
    @JoinColumn(name = "default_language_id")
    private Language defaultLanguage;

    @ManyToOne
    @JoinColumn(name = "default_dark_theme_id")
    private Theme defaultDarkTheme;

    @ManyToOne
    @JoinColumn(name = "default_light_theme_id")
    private Theme defaultLightTheme;

    @ManyToOne
    @JoinColumn(name = "logo_id")
    private File logo;

    @ManyToOne
    @JoinColumn(name = "registration_type_id")
    private RegistrationType registrationType;

    @OneToMany(mappedBy = "edition")
    private List<Registration> registrations;

    @OneToMany(mappedBy = "edition")
    private List<Voucher> vouchers;

    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "edition")
    private List<Caravan> caravans;

    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "edition")
    private List<UserPermission> userPermissions;

    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "edition")
    private List<Certificate> certificates;

    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "edition")
    private List<Track> tracks;

    public boolean hasUserWithCustomCaravanEnrollment(Long userId, boolean confirmed, boolean accepted, boolean payed) {
        if (caravans == null) return false;
        return caravans
            .stream()
            .anyMatch(caravan -> caravan
                .getCaravanEnrollments()
                .stream()
                .anyMatch(caravanEnrollment ->
                    caravanEnrollment.getUser().getId().equals(userId) &&
                        caravanEnrollment.getConfirmed() == confirmed &&
                        caravanEnrollment.getAccepted() == accepted &&
                        caravanEnrollment.getPayed() == payed
                )
            );
    }

}
