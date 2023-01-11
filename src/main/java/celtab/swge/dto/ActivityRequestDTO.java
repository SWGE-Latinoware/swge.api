package celtab.swge.dto;

import celtab.swge.model.enums.ActivityType;
import celtab.swge.model.enums.EditionType;
import celtab.swge.util.HashMapConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActivityRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String language;

    private String languageFlag;

    @Column(nullable = false)
    private ActivityType type;

    @Column(nullable = false)
    private Integer vacancies;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private EditionType presentationType;

    @Column(nullable = false)
    private String workload;

    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> description;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private TrackRequestDTO track;

    @ManyToOne
    @JoinColumn(name = "attendee_certificate_id")
    private CertificateRequestDTO attendeeCertificate;

    @ManyToOne
    @JoinColumn(name = "speaker_certificate_id")
    private CertificateRequestDTO speakerCertificate;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private SpaceRequestDTO place;

    private String placeUrl;

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private UserRequestDTO responsibleUser;

    @OneToMany(mappedBy = "activity")
    private List<SpeakerActivityRequestDTO> speakers;

    @OneToMany(mappedBy = "activity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ScheduleRequestDTO> schedule;
}
