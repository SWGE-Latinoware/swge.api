package celtab.swge.model.enrollment;

import celtab.swge.model.BasicModel;
import celtab.swge.model.Caravan;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class BasicEnrollment extends BasicModel<Long> {

    @ManyToOne
    @JoinColumn(name = "caravan_id")
    private Caravan caravan;

    @Column(nullable = false)
    private Boolean accepted;

    @Column(nullable = false)
    private Boolean payed;

}
