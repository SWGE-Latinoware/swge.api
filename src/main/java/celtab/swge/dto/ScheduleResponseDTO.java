package celtab.swge.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScheduleResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private Date startDateTime;

    @Column(nullable = false)
    private Date endDateTime;

    @Column(nullable = false)
    private Boolean allDay;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @ToString.Exclude
    private ActivityResponseDTO activity;

    @Transient
    private String color;

    @Transient
    private String title;

}
