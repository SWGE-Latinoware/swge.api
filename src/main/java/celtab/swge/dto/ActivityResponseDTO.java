package celtab.swge.dto;

import celtab.swge.model.enums.ActivityType;
import celtab.swge.model.enums.EditionType;
import celtab.swge.util.HashMapConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActivityResponseDTO extends GenericDTO<Long> {

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

    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> description;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private TrackResponseDTO track;

    @ManyToOne
    @JoinColumn(name = "attendee_certificate_id")
    private CertificateResponseDTO attendeeCertificate;

    @ManyToOne
    @JoinColumn(name = "speaker_certificate_id")
    private CertificateResponseDTO speakerCertificate;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private SpaceResponseDTO place;

    private String placeUrl;

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private UserResponseDTO responsibleUser;

    @OneToMany(mappedBy = "activity")
    @JsonIgnoreProperties(value = {"activity"})
    private List<SpeakerActivityResponseDTO> speakers;

    @OneToMany(mappedBy = "activity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"activity"})
    private List<ScheduleResponseDTO> schedule;

}
