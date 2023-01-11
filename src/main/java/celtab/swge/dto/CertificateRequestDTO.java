package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CertificateRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

    @ColumnDefault("true")
    @Column(nullable = false)
    private Boolean allowQrCode;

    @ManyToOne
    @JoinColumn(name = "background_image_id", nullable = false)
    private FileRequestDTO backgroundImage;

    @Column(nullable = false)
    private Date availabilityDateTime;

    @OneToMany(mappedBy = "certificate", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<DynamicContentRequestDTO> dynamicContents;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private EditionRequestDTO edition;
}
