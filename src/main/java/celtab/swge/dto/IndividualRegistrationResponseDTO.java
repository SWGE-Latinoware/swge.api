package celtab.swge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class IndividualRegistrationResponseDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private ActivityResponseDTO activity;

    @ManyToOne
    @JoinColumn(name = "registration_id")
    @ToString.Exclude
    private RegistrationResponseDTO registration;

    @OneToMany(mappedBy = "individualRegistration", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"individualRegistration"})
    private List<IndividualRegistrationScheduleResponseDTO> individualRegistrationScheduleList;

    @Column(nullable = false)
    private Boolean customSchedule;
}
