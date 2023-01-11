package celtab.swge.model;

import celtab.swge.model.enums.ActivityType;
import celtab.swge.model.enums.EditionType;
import celtab.swge.model.registration.individual_registration.IndividualRegistration;
import celtab.swge.model.registration.individual_registration.TutoredIndividualRegistration;
import celtab.swge.model.user.User;
import celtab.swge.util.HashMapConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "edition_id"})
})
public class Activity extends BasicModel<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String language;

    private String languageFlag;

    @Column(nullable = false)
    private ActivityType type;

    @Column(nullable = false)
    private Integer vacancies;

    @Transient
    private Integer remainingVacancies;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private EditionType presentationType;

    @Column(nullable = false)
    private String workload;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> description;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;

    @ManyToOne
    @JoinColumn(name = "attendee_certificate_id")
    private Certificate attendeeCertificate;

    @ManyToOne
    @JoinColumn(name = "speaker_certificate_id")
    private Certificate speakerCertificate;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Space place;

    private String placeUrl;

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private User responsibleUser;

    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "activity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<SpeakerActivity> speakers;

    @OneToMany(mappedBy = "activity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Schedule> schedule;

    @OneToMany(mappedBy = "activity")
    private List<IndividualRegistration> individualRegistrations;

    @OneToMany(mappedBy = "activity")
    private List<TutoredIndividualRegistration> tutoredIndividualRegistrations;

    @PrePersist
    @PreUpdate
    private void updateVacanciesAndPrice() {
        if (isLecture()) {
            price = 0d;
            vacancies = Integer.MAX_VALUE;
        }
    }

    public boolean isLecture() {
        return ActivityType.LECTURE.equals(type) || ActivityType.KEYNOTE.equals(type);
    }

    @PostLoad
    private void loadRemainingVacancies() {
        var remaining = track
            .getEdition()
            .getRegistrations()
            .stream()
            .mapToInt(registration -> registration
                .getIndividualRegistrations()
                .stream()
                .mapToInt(individualRegistration -> individualRegistration.getActivity().getId().equals(getId()) ? 1 : 0)
                .sum()
            ).sum();
        remainingVacancies = vacancies - remaining;
    }

    public boolean hasVacancies() {
        return remainingVacancies > 0;
    }
}
