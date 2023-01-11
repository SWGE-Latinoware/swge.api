package celtab.swge.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Schedule extends BasicModel<Long> {

    @Column(nullable = false)
    private Date startDateTime;

    @Column(nullable = false)
    private Date endDateTime;

    @Column(nullable = false)
    private Boolean allDay;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Transient
    private String color;

    @Transient
    private String title;

    @PostLoad
    private void loadColorAndTitle() {
        title = activity.getName();
        color = activity.getTrack().getCalendarColor();
    }

}
