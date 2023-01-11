package celtab.swge.model.user;

import celtab.swge.model.BasicModel;
import celtab.swge.model.File;
import celtab.swge.model.enums.ExclusionStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;


@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Exclusion extends BasicModel<Long> {

    @Column(nullable = false)
    private Date registryDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tutored_user_id")
    private TutoredUser tutoredUser;

    @ManyToOne
    @JoinColumn(name = "delete_request_id", nullable = false)
    private DeleteRequest deleteRequest;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private ExclusionStatus status;

    private String note;

    @ManyToOne
    @JoinColumn(name = "dpo_id")
    private User dpo;

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private File attachment;

    private Date returnDate;

    private Date deadlineExclusionDate;

    private Date effectiveDeletionDate;
}
