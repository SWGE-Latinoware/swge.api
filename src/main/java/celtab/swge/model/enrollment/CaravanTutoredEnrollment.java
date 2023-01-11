package celtab.swge.model.enrollment;

import celtab.swge.model.user.TutoredUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class CaravanTutoredEnrollment extends BasicEnrollment {

    @ManyToOne
    @JoinColumn(name = "tutored_user_id")
    private TutoredUser tutoredUser;
}
