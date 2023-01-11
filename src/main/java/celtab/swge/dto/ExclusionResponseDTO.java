package celtab.swge.dto;

import celtab.swge.model.enums.ExclusionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExclusionResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private Date registryDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties(value = {"exclusionRequests"})
    private UserResponseDTO user;

    @ManyToOne
    @JoinColumn(name = "tutored_user_id")
    @JsonIgnoreProperties(value = {"exclusionRequests"})
    private TutoredUserResponseDTO tutoredUser;

    @ManyToOne
    @JoinColumn(name = "delete_request_id", nullable = false)
    @JsonIgnoreProperties(value = {"exclusions"})
    private DeleteRequestResponseDTO deleteRequest;

    private ExclusionStatus status;

    private String note;

    @ManyToOne
    @JoinColumn(name = "dpo_id")
    private UserResponseDTO dpo;

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private FileResponseDTO attachment;

    private Date returnDate;

    private Date deadlineExclusionDate;

    private Date effectiveDeletionDate;
}
