package celtab.swge.model;

import celtab.swge.util.HashMapConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "edition_id"})
})
public class Track extends BasicModel<Long> {

    @Column(nullable = false)
    private String name;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> description;

    @Column(nullable = false)
    private Date initialDate;

    @Column(nullable = false)
    private Date finalDate;

    private String calendarColor;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;

    @ManyToOne
    @JoinColumn(name = "attendee_certificate_id")
    private Certificate attendeeCertificate;

    @ManyToOne
    @JoinColumn(name = "speaker_certificate_id")
    private Certificate speakerCertificate;

    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "track")
    private List<Activity> activities;

}
