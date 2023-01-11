package celtab.swge.dto;

import celtab.swge.model.enums.ExclusionStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExclusionRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private Date registryDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserRequestDTO user;

    @ManyToOne
    @JoinColumn(name = "tutored_user_id")
    private TutoredUserRequestDTO tutoredUser;

    @ManyToOne
    @JoinColumn(name = "delete_request_id", nullable = false)
    private DeleteRequestRequestDTO deleteRequest;

    private ExclusionStatus status;

    private String note;

    @ManyToOne
    @JoinColumn(name = "dpo_id")
    private UserRequestDTO dpo;

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private FileRequestDTO attachment;

    private Date returnDate;

    private Date deadlineExclusionDate;

    private Date effectiveDeletionDate;
}
