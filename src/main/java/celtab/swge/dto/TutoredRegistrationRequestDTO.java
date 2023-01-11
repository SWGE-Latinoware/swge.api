package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class TutoredRegistrationRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String userRegistrationId;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private EditionRequestDTO edition;

    @ManyToOne
    @JoinColumn(name = "tutored_user_id")
    private TutoredUserRequestDTO tutoredUser;

}
