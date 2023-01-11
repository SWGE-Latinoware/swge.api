package celtab.swge.dto;

import celtab.swge.util.HashMapConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrackResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

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
    private EditionResponseDTO edition;

    @ManyToOne
    @JoinColumn(name = "attendee_certificate_id")
    private CertificateResponseDTO attendeeCertificate;

    @ManyToOne
    @JoinColumn(name = "speaker_certificate_id")
    private CertificateResponseDTO speakerCertificate;

}
