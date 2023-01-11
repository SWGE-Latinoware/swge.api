package celtab.swge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TutoredRegistrationResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private Boolean payed;

    @Column(nullable = false)
    private Double originalPrice;

    @Column(nullable = false)
    private Double finalPrice;

    @Column(nullable = false)
    private String userRegistrationId;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private EditionResponseDTO edition;

    @ManyToOne
    @JoinColumn(name = "tutored_user_id")
    private TutoredUserResponseDTO tutoredUser;

    @Column(nullable = false)
    private Date registrationDateTime;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"registration"})
    private List<TutoredIndividualRegistrationResponseDTO> individualRegistrations;
}
