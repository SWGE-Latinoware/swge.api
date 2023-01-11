package celtab.swge.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "edition_id"})
})
public class Certificate extends BasicModel<Long> {

    @Column(nullable = false)
    private String name;

    @ColumnDefault("true")
    @Column(nullable = false)
    private Boolean allowQrCode;

    @ManyToOne
    @JoinColumn(name = "background_image_id", nullable = false)
    private File backgroundImage;

    @Column(nullable = false)
    private Date availabilityDateTime;

    @OneToMany(mappedBy = "certificate", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<DynamicContent> dynamicContents;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;
}
