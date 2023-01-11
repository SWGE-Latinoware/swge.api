package celtab.swge.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "institution_id"})
})
public class Space extends BasicModel<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String number;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;
}
